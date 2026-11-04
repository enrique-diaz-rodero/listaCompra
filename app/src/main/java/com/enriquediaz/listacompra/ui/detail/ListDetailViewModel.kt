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
    private val productSearchRepository: ProductSearchRepository, // <-- NUEVO
    private val preferencesRepository: UserPreferencesRepository, // <-- NUEVO
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val listId: String = savedStateHandle.get<String>("listId") ?: ""
    private val currentUserId: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""

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

    // --- NUEVOS ESTADOS PARA EL BUSCADOR DE LA API ---
    private val _searchResults = MutableStateFlow<List<OffProduct>>(emptyList())
    val searchResults: StateFlow<List<OffProduct>> = _searchResults
    private var searchJob: Job? = null // Para cancelar búsquedas anteriores si el usuario escribe rápido

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

    // --- NUEVA FUNCIÓN: Búsqueda en Open Food Facts ---
    fun searchApi(query: String) {
        // Cancelamos la búsqueda anterior si el usuario sigue escribiendo
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            if (query.length > 2) {
                // Obtenemos el supermercado preferido del usuario
                val prefs = preferencesRepository.userPreferencesFlow.first()
                val supermarket = prefs.supermarket

                val result = productSearchRepository.searchProducts(query, supermarket)
                if (result.isSuccess) {
                    _searchResults.value = result.getOrDefault(emptyList())
                } else {
                    _searchResults.value = emptyList()
                }
            } else {
                _searchResults.value = emptyList()
            }
        }
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
        searchJob?.cancel()
    }

    // --- GESTIÓN DE MIEMBROS ---
    fun loadMembers() {
        viewModelScope.launch {
            listRepository.getListById(listId).onSuccess { list ->
                _listDetails.value = list
                val users = mutableListOf<User>()
                list.members.forEach { uid ->
                    listRepository.getUserDetails(uid).onSuccess { user ->
                        users.add(user)
                    }
                }
                _membersDetails.value = users
            }
        }
    }

    fun removeMember(memberUid: String) {
        viewModelScope.launch {
            listRepository.removeMember(listId, memberUid).onSuccess {
                loadMembers()
            }
        }
    }

    fun getCurrentUserId(): String = currentUserId

    // --- GESTIÓN DE CATEGORÍAS Y PRODUCTOS ---
    fun addCategory(name: String) {
        viewModelScope.launch {
            listRepository.addCategory(listId, name)
        }
    }

    fun addProduct(name: String, categoryId: String) {
        viewModelScope.launch {
            listRepository.addProduct(listId, name, categoryId)
        }
    }

    fun toggleProduct(product: Product) {
        val nuevoEstado = !product.isCompleted

        // Optimistic UI
        _products.value = _products.value.map {
            if (it.id == product.id) it.copy(isCompleted = nuevoEstado) else it
        }

        viewModelScope.launch {
            listRepository.toggleProductCompleted(listId, product.id, nuevoEstado)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            listRepository.deleteProduct(listId, product.id)
        }
    }

    // --- COMPARTIR LISTA ---
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