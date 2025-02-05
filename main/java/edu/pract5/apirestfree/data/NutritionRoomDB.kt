package edu.pract5.apirestfree.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import edu.pract5.apirestfree.model.Food

/**
 * Clase NutritionRoomDB.kt
 * Se encarga de definir la base de datos de la aplicación
 * @Author Jonathan Gómez Fraile
 */
@Database(entities = [Food::class], version = 1)
abstract class NutritionRoomDB : RoomDatabase() {
    abstract fun foodDao(): FoodDao
}

/**
 * Interface FoodDao.kt
 * Se encarga de definir las funciones que se van a utilizar de la base de datos
 * @Author Jonathan Gómez Fraile
 */
@Dao
interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(foods : List<Food>)

    @Query("SELECT * FROM food ORDER BY name ASC")
    suspend fun getFoods(): List<Food>

    @Query("UPDATE food SET isFavorite = :isFavorite WHERE name = :name")
    suspend fun updateFavorite(name: String, isFavorite: Boolean)

    @Query("SELECT * FROM food WHERE isFavorite = 1")
    suspend fun getFavorites(): List<Food>

    @Update
    suspend fun updateFood(food: Food)

    @Delete
    suspend fun deleteFood(food: Food)

}
