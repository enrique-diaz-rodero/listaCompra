package com.enriquediaz.listacompra.ui.detail

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

    fun createBaseCategories() {
        viewModelScope.launch {
            val baseCats = listOf(
                "Lácteos y Huevos", "Carnicería", "Pescadería", "Panadería",
                "Frutas y Verduras", "Despensa", "Bebidas", "Limpieza", "Higiene"
            )
            baseCats.forEach { name ->
                if (_categories.value.none { it.name == name }) {
                    listRepository.addCategory(listId, name)
                }
            }
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

    // NUEVO: Eliminar categoría
    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            listRepository.deleteCategory(listId, category.id)
        }
    }

    fun addProduct(
        name: String, categoryId: String, ean: String? = null, price: Double? = null, imageUrl: String? = null
    ) {
        viewModelScope.launch {
            listRepository.addProduct(listId, name, categoryId, ean, price, imageUrl)
            _currentPrice.value = null
        }
    }

    fun updateProduct(productId: String, newName: String, newCategoryId: String) {
        viewModelScope.launch {
            listRepository.updateProduct(listId, productId, newName, newCategoryId)
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
            } else {
                _shareResult.value = result.exceptionOrNull()?.message ?: "Error"
            }
        }
    }

    fun clearShareResult() {
        _shareResult.value = null
    }
}