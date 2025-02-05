package edu.pract5.apirestfree

import android.app.Application
import androidx.room.Room
import edu.pract5.apirestfree.data.NutritionRoomDB

/**
 * Class NutritionApp
 * se encarga de inicializar la base de datos
 * @property nutritionDB base de datos
 * @constructor crea la base de datos
 * @autor Jonathan Gómez Fraile
 */
class NutritionApp : Application() {
    lateinit var nutritionDB: NutritionRoomDB
        private set

    override fun onCreate() {
        super.onCreate()
        nutritionDB = Room.databaseBuilder(
            this,
            NutritionRoomDB::class.java, "nutrition.db"
        ).build()
    }
}
