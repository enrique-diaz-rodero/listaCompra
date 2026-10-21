package com.enriquediaz.listacompra.ui.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquediaz.listacompra.data.model.ShoppingList
import com.enriquediaz.listacompra.data.repository.ListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val listRepository: ListRepository
) : ViewModel() {

    private val _lists = MutableStateFlow<List<ShoppingList>>(emptyList())
    val lists: StateFlow<List<ShoppingList>> = _lists

    fun loadLists() {
        viewModelScope.launch {
            val result = listRepository.getUserLists()
            if (result.isSuccess) {
                _lists.value = result.getOrDefault(emptyList())
            }
        }
    }

    fun createNewList(name: String) {
        viewModelScope.launch {
            listRepository.createList(name)
            loadLists() // Recargar la lista tras crear
        }
    }
}