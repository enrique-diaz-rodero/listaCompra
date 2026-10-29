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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListDetailViewModel @Inject constructor(
    private val listRepository: ListRepository,
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

    fun addCategory(name: String) {
        viewModelScope.launch {
            listRepository.addCategory(listId, name)
            // El flujo de observeData() actualizará la UI automáticamente
        }
    }

    fun addProduct(name: String, categoryId: String) {
        viewModelScope.launch {
            listRepository.addProduct(listId, name, categoryId)
            // El flujo de observeData() actualizará la UI automáticamente
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
            // Si falla, el flujo en tiempo real revertirá la UI a su estado real de la nube
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            listRepository.deleteProduct(listId, product.id)
            // La UI se actualizará sola por el flujo
        }
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