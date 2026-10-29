package com.enriquediaz.listacompra.ui.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquediaz.listacompra.data.model.ShoppingList
import com.enriquediaz.listacompra.data.repository.AuthRepository
import com.enriquediaz.listacompra.data.repository.ListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val listRepository: ListRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _lists = MutableStateFlow<List<ShoppingList>>(emptyList())
    val lists: StateFlow<List<ShoppingList>> = _lists

    init {
        observeLists() // Se ejecuta al nacer el ViewModel
    }

    private fun observeLists() {
        viewModelScope.launch {
            // collectLatest se queda escuchando para siempre los cambios
            listRepository.getUserListsFlow().collectLatest { listsFromCloud ->
                _lists.value = listsFromCloud
            }
        }
    }

    fun createNewList(name: String) {
        viewModelScope.launch {
            listRepository.createList(name)
            // Ya NO llamamos a loadLists(). El flujo de arriba detectará el cambio solo.
        }
    }

    fun deleteList(listId: String) {
        viewModelScope.launch {
            listRepository.deleteList(listId)
            // El flujo también lo quitará de la pantalla automáticamente.
        }
    }

    fun logout() {
        authRepository.signOut()
    }
}