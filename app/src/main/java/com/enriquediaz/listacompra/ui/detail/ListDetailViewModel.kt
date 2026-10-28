package com.enriquediaz.listacompra.ui.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquediaz.listacompra.data.model.Category
import com.enriquediaz.listacompra.data.model.Product
import com.enriquediaz.listacompra.data.model.ShoppingList
import com.enriquediaz.listacompra.data.model.User
import com.enriquediaz.listacompra.data.repository.ListRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListDetailViewModel @Inject constructor(
    private val listRepository: ListRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val listId: String = savedStateHandle.get<String>("listId") ?: ""
    private val currentUserId: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // --- ESTADOS DE LA UI ---
    private val _shareResult = MutableStateFlow<String?>(null)
    val shareResult: StateFlow<String?> = _shareResult

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    // NUEVOS ESTADOS: Para gestionar los miembros y los detalles de la lista
    private val _listDetails = MutableStateFlow<ShoppingList?>(null)
    val listDetails: StateFlow<ShoppingList?> = _listDetails

    private val _membersDetails = MutableStateFlow<List<User>>(emptyList())
    val membersDetails: StateFlow<List<User>> = _membersDetails

    init {
        Log.d("ListDetailVM", "ID de la lista: '$listId'")
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            listRepository.getCategories(listId).onSuccess { _categories.value = it }
            listRepository.getProducts(listId).onSuccess { _products.value = it }
            loadMembers() // Cargamos los miembros al iniciar
        }
    }

    // NUEVA FUNCIÓN: Cargar los datos de los miembros
    fun loadMembers() {
        viewModelScope.launch {
            listRepository.getListById(listId).onSuccess { list ->
                _listDetails.value = list
                val users = mutableListOf<User>()

                // Buscamos los datos de cada UID en la colección "users"
                list.members.forEach { uid ->
                    listRepository.getUserDetails(uid).onSuccess { user ->
                        users.add(user)
                    }
                }
                _membersDetails.value = users
            }.onFailure {
                Log.e("ListDetailVM", "Error cargando miembros: ${it.message}")
            }
        }
    }

    // NUEVA FUNCIÓN: Eliminar a un miembro
    fun removeMember(memberUid: String) {
        viewModelScope.launch {
            val result = listRepository.removeMember(listId, memberUid)
            if (result.isSuccess) {
                loadMembers() // Recargamos la lista de miembros para que desaparezca de la pantalla
            } else {
                Log.e("ListDetailVM", "No se pudo eliminar al miembro")
            }
        }
    }

    // NUEVA FUNCIÓN: Obtener tu propio ID
    fun getCurrentUserId(): String = currentUserId

    // --- FUNCIONES EXISTENTES ---

    fun addCategory(name: String) {
        viewModelScope.launch {
            listRepository.addCategory(listId, name)
            loadData()
        }
    }

    fun addProduct(name: String, categoryId: String) {
        viewModelScope.launch {
            listRepository.addProduct(listId, name, categoryId)
            loadData()
        }
    }

    fun toggleProduct(product: Product) {
        val nuevoEstado = !product.isCompleted

        // 1. Actualizamos la pantalla inmediatamente (Optimistic UI)
        _products.value = _products.value.map {
            if (it.id == product.id) it.copy(isCompleted = nuevoEstado) else it
        }

        // 2. Guardamos en Firestore en segundo plano
        viewModelScope.launch {
            val result = listRepository.toggleProductCompleted(listId, product.id, nuevoEstado)
            if (result.isFailure) {
                Log.e("ListDetailVM", "Revirtiendo cambio visual por fallo en BD")
                _products.value = _products.value.map {
                    if (it.id == product.id) it.copy(isCompleted = product.isCompleted) else it
                }
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            val result = listRepository.deleteProduct(listId, product.id)
            if (result.isSuccess) {
                loadData()
            } else {
                Log.e("ListDetailVM", "No se pudo borrar el producto")
            }
        }
    }

    fun shareList(email: String) {
        viewModelScope.launch {
            val result = listRepository.shareListWithEmail(listId, email)
            if (result.isSuccess) {
                _shareResult.value = "Lista compartida con éxito"
                loadData()
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Error desconocido al compartir"
                _shareResult.value = errorMsg
            }
        }
    }

    fun clearShareResult() {
        _shareResult.value = null
    }
}