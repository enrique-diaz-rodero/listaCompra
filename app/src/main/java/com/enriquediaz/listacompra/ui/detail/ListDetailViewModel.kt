package com.enriquediaz.listacompra.ui.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquediaz.listacompra.data.model.Category
import com.enriquediaz.listacompra.data.model.Product
import com.enriquediaz.listacompra.data.repository.ListRepository
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

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    init {
        Log.d("ListDetailVM", "ID de la lista: '$listId'")
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            listRepository.getCategories(listId).onSuccess { _categories.value = it }
            listRepository.getProducts(listId).onSuccess { _products.value = it }
        }
    }

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
                // Si falla, deshacemos el cambio visual
                Log.e("ListDetailVM", "Revirtiendo cambio visual por fallo en BD")
                _products.value = _products.value.map {
                    if (it.id == product.id) it.copy(isCompleted = product.isCompleted) else it
                }
            }
        }
    }
}