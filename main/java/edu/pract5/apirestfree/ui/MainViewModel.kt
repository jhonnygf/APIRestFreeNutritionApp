package edu.pract5.apirestfree.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.pract5.apirestfree.data.Repository
import edu.pract5.apirestfree.model.Food
import edu.pract5.apirestfree.utils.FilterFoods
import edu.pract5.apirestfree.utils.filterFoods
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Clase MainViewModel.kt
 * Clase que se encarga de la logica de la vista principal
 * @param repository: Repository objeto que permite interactuar con los datos
 * @return MainViewModel instancia de MainViewModel
 * @Author Jonathan Gómez Fraile
 */
class MainViewModel(private val repository: Repository) : ViewModel() {

    private val _foods = MutableStateFlow<List<Food>>(emptyList())
    val foods: StateFlow<List<Food>> get() = _foods

    /**
     * Funcion que permite obtener todos los alimentos buscados en la API
     */
    fun getFoods() {
        viewModelScope.launch {
            repository.fetchFoods().collect { apiFoods ->
                _foods.value = apiFoods
            }
        }
    }

    /**
     * Función que permite cargar los alimentos favoritos de la base de datos local
     */
    private fun loadFavorites() {
        viewModelScope.launch {
            val favoriteFoods = repository.getFavorites()
            _foods.value = favoriteFoods
        }
    }

    /**
     * Función que permite cargar los alimentos  de la base de datos local
     */
    private fun loadLocalFoods() {
        viewModelScope.launch {
            val localFoods = repository.getLocalFoods()
            _foods.value = localFoods
        }
    }

    /**
     * Función que permite actualizar el estado de favorito de un alimento
     */
    fun updateFood(food: Food) {
        viewModelScope.launch {
            val updatedFood = food.copy(isFavorite = food.isFavorite)
            repository.saveFood(updatedFood)
            loadFilteredFoods()
        }
    }

    /**
     * Función que permite cargar alimentos filtrados
     */
    fun loadFilteredFoods() {
        viewModelScope.launch {
            when (filterFoods) {
                FilterFoods.ALL -> loadLocalFoods()
                FilterFoods.FAVORITES -> loadFavorites()
            }
        }
    }

    /**
     * Función que permite buscar alimentos por nombre
     */
    fun searchFood(query: String) {
        viewModelScope.launch {
            val result = repository.searchFoodByName(query)
            if (result.isNotEmpty()) {
                //Combina las listas
                val updatedList = _foods.value.toMutableList().apply {
                    addAll(result.filterNot { food -> this.any { it.name == food.name } })
                }
                _foods.value = updatedList

                // Guarda los nuevos en la base de datos local
                result.forEach { food ->
                    repository.saveFood(food)
                }
            }
        }
    }
}

/**
 * Clase MainViewModelFactory.kt
 * Clase que se encarga de crear una instancia de MainViewModel
 * @param repository: Repository objeto que permite interactuar con los datos
 * @return MainViewModel instancia de MainViewModel
 * @Author Jonathan Gómez Fraile
 */
@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val repository: Repository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}