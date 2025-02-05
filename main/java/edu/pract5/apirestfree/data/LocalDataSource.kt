package edu.pract5.apirestfree.data

import edu.pract5.apirestfree.model.Food

/**
 * Clase LocalDataSource.kt
 * Se encarga de obtener los datos de la base de datos local
 * @param db: FoodDao objeto que permite interactuar con la base de datos
 * @Author Jonathan Gómez Fraile
 */
class LocalDataSource(val db: FoodDao) {

    suspend fun insertFood(food: Food) {
       return  db.insertFood(listOf(food))
    }

    suspend fun getFoods(): List<Food> = db.getFoods()

}
