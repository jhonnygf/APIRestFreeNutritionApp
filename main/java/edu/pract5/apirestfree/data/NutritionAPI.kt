package edu.pract5.apirestfree.data

import edu.pract5.apirestfree.BuildConfig
import edu.pract5.apirestfree.model.Food
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * class NutritionAPI.kt
 * Se encarga de obtener la instancia de la API
 * @Author Jonathan Gómez Fraile
 */
class NutritionAPI {
    companion object {
        const val BASE_URL = "https://api.api-ninjas.com/"

        fun getRetrofit2Api(): NutritionApiInterface {
            return Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(NutritionApiInterface::class.java)
        }
    }
}

/**
 * interface Retrofit2ApiInterface
 * Se encarga de definir las funciones que se van a utilizar de la API
 * @Author Jonathan Gómez Fraile
 */
interface NutritionApiInterface {

    @Headers("X-Api-Key: ${BuildConfig.API_KEY}")
    @GET("v1/nutrition")
    suspend fun getFoods(): List<Food>

    @Headers("X-Api-Key: ${BuildConfig.API_KEY}")
    @GET("v1/nutrition")
    suspend fun searchFoodByName(
        @Query("query") foodName: String
    ): List<Food>
}