package com.enriquediaz.listacompra.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquediaz.listacompra.data.model.Category
import com.enriquediaz.listacompra.data.model.Product
import com.enriquediaz.listacompra.data.model.ShoppingList
import com.enriquediaz.listacompra.data.model.User
import com.enriquediaz.listacompra.data.remote.OffProduct
import com.enriquediaz.listacompra.data.repository.ListRepository
import com.enriquediaz.listacompra.data.repository.PriceRepository
import com.enriquediaz.listacompra.data.repository.ProductSearchRepository
import com.enriquediaz.listacompra.data.repository.UserPreferencesRepository
import com.enriquediaz.listacompra.utils.AutoCategorizer // <-- AÑADIDO PARA LA CATEGORÍA POR DEFECTO
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListDetailViewModel @Inject constructor(
    private val listRepository: ListRepository,
    private val productSearchRepository: ProductSearchRepository,
    private val priceRepository: PriceRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val analytics: FirebaseAnalytics,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val listId: String = savedStateHandle.get<String>("listId") ?: ""
    private val currentUserId: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val userPreferences = preferencesRepository.userPreferencesFlow

    private val _shareResult = MutableStateFlow<String?>(null)
    val shareResult: StateFlow<String?> = _shareResult

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _listDetails = MutableStateFlow<ShoppingList?>(null)
    val listDetails: StateFlow<ShoppingList?> = _listDetails

    private val _membersDetails = MutableStateFlow<List<User>>(emptyList())
    val membersDetails: StateFlow<List<User>> = _membersDetails

    private val _searchResults = MutableStateFlow<List<OffProduct>>(emptyList())
    val searchResults: StateFlow<List<OffProduct>> = _searchResults
    private var searchJob: Job? = null

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _currentPrice = MutableStateFlow<Double?>(null)
    val currentPrice: StateFlow<Double?> = _currentPrice

    init {
        observeData()
        loadMembers()
    }

    private fun observeData() {
        viewModelScope.launch {
            listRepository.getProductsFlow(listId).collectLatest { _products.value = it }
        }
        viewModelScope.launch {
            listRepository.getCategoriesFlow(listId).collectLatest { _categories.value = it }
        }
    }

    fun searchApi(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.length > 2) {
                _isSearching.value = true
                val prefs = preferencesRepository.userPreferencesFlow.first()
                val result = productSearchRepository.searchProducts(query, prefs.supermarket)
                if (result.isSuccess) {
                    _searchResults.value = result.getOrDefault(emptyList())
                } else {
                    _searchResults.value = emptyList()
                }
                _isSearching.value = false
            } else {
                _searchResults.value = emptyList()
                _isSearching.value = false
            }
        }
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
        searchJob?.cancel()
        _isSearching.value = false
    }

    fun fetchPrice(ean: String?) {
        if (ean.isNullOrBlank()) {
            _currentPrice.value = null
            return
        }
        viewModelScope.launch {
            val prefs = preferencesRepository.userPreferencesFlow.first()
            if (prefs.showPrices) {
                val result = priceRepository.getPriceForProduct(ean, prefs.supermarket)
                _currentPrice.value = result.getOrNull()
            } else {
                _currentPrice.value = null
            }
        }
    }

    fun clearPrice() {
        _currentPrice.value = null
    }

    fun loadMembers() {
        viewModelScope.launch {
            listRepository.getListById(listId).onSuccess { list ->
                _listDetails.value = list
                val users = mutableListOf<User>()
                list.members.forEach { uid ->
                    listRepository.getUserDetails(uid).onSuccess { user -> users.add(user) }
                }
                _membersDetails.value = users
            }
        }
    }

    fun removeMember(memberUid: String) {
        viewModelScope.launch {
            listRepository.removeMember(listId, memberUid).onSuccess { loadMembers() }
        }
    }

    fun getCurrentUserId(): String = currentUserId

    fun addCategory(name: String) {
        viewModelScope.launch { listRepository.addCategory(listId, name) }
    }

    // FUNCIÓN CORREGIDA
    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            // Buscamos el ID de la categoría por defecto para poder mover los productos allí
            val defaultCategory = _categories.value.find {
                it.name == AutoCategorizer.DEFAULT_CATEGORY_NAME
            }

            if (defaultCategory != null) {
                // Ahora le pasamos los 3 parámetros requeridos
                listRepository.deleteCategory(listId, category.id, defaultCategory.id)
            } else {
                Log.e("ListDetailVM", "Error: No se encontró la categoría por defecto para mover los productos.")
            }
        }
    }

    fun addProduct(
        name: String, categoryId: String, ean: String? = null, price: Double? = null, imageUrl: String? = null
    ) {
        viewModelScope.launch {
            listRepository.addProduct(listId, name, categoryId, ean, price, imageUrl)

            // Registro de evento en Analytics
            analytics.logEvent("product_added", Bundle().apply {
                putString("product_name", name)
                putBoolean("has_price", price != null)
                putBoolean("has_image", imageUrl != null)
            })

            _currentPrice.value = null
        }
    }

    fun updateProduct(productId: String, newName: String, newCategoryId: String) {
        viewModelScope.launch {
            listRepository.updateProduct(listId, productId, newName, newCategoryId)

            // Registro de evento en Analytics
            analytics.logEvent("product_updated", Bundle().apply {
                putString("new_name", newName)
            })
        }
    }

    fun toggleProduct(product: Product) {
        val nuevoEstado = !product.isCompleted
        _products.value = _products.value.map {
            if (it.id == product.id) it.copy(isCompleted = nuevoEstado) else it
        }
        viewModelScope.launch {
            listRepository.toggleProductCompleted(listId, product.id, nuevoEstado)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch { listRepository.deleteProduct(listId, product.id) }
    }

    fun shareList(email: String) {
        viewModelScope.launch {
            val result = listRepository.shareListWithEmail(listId, email)
            if (result.isSuccess) {
                _shareResult.value = "Lista compartida con éxito"
                loadMembers()

                // Registro de evento en Analytics
                analytics.logEvent("list_shared", Bundle().apply {
                    putString("shared_with", email)
                })

            } else {
                _shareResult.value = result.exceptionOrNull()?.message ?: "Error"
            }
        }
    }

    fun clearShareResult() {
        _shareResult.value = null
    }
}