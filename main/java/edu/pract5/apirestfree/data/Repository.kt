package edu.pract5.apirestfree.data

import edu.pract5.apirestfree.model.Food
import kotlinx.coroutines.flow.Flow

/**
 * class Repository.kt
 * Se encarga de obtener los datos de la API y de la base de datos local
 * @param remoteDataSource: RemoteDataSource objeto que permite interactuar con la API
 * @param localDataSource: LocalDataSource objeto que permite interactuar con la base de datos local
 * @Author Jonathan Gómez Fraile
 */
class Repository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) {

    suspend fun searchFoodByName(name: String): List<Food> {
        return remoteDataSource.getFoodByName(name)
    }

    suspend fun saveFood(food: Food) {
        localDataSource.insertFood(food)
    }

    suspend fun getLocalFoods(): List<Food> {
        return localDataSource.getFoods()
    }

    suspend fun getFavorites(): List<Food> {
        return localDataSource.db.getFavorites()
    }

    fun fetchFoods(): Flow<List<Food>> = remoteDataSource.getFoods()

}