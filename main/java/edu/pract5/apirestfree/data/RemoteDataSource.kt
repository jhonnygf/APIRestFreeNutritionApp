package edu.pract5.apirestfree.data

import edu.pract5.apirestfree.model.Food
import kotlinx.coroutines.flow.flow

/**
 * Clase RemoteDataSource.kt
 * Se encarga de obtener los datos de la API
 * @author Jonathan Gómez Fraile
 */
class RemoteDataSource {

    private val api = NutritionAPI.getRetrofit2Api()

    fun getFoods() = flow{
        emit(api.getFoods())
    }

    suspend fun getFoodByName(name: String): List<Food> = api.searchFoodByName(name)
}
