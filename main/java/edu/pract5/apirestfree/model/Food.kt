package edu.pract5.apirestfree.model


import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Data class Food
 * Se encarga de definir la estructura de un alimento
 * @author Jonathan Gómez Fraile
 */
@Parcelize
@Entity(primaryKeys = ["name"])
data class Food(
    @SerializedName("calories")
    val calories: String?,
    @SerializedName("carbohydrates_total_g")
    val carbohydratesTotalG: Double?,
    @SerializedName("cholesterol_mg")
    val cholesterolMg: Double?,
    @SerializedName("fat_saturated_g")
    val fatSaturatedG: Double?,
    @SerializedName("fat_total_g")
    val fatTotalG: Double?,
    @SerializedName("fiber_g")
    val fiberG: Double?,
    @SerializedName("name")
    val name: String,
    @SerializedName("potassium_mg")
    val potassiumMg: Double?,
    @SerializedName("protein_g")
    val proteinG: String?,
    @SerializedName("serving_size_g")
    val servingSizeG: String?,
    @SerializedName("sodium_mg")
    val sodiumMg: Double?,
    @SerializedName("sugar_g")
    val sugarG: Double?,
    var isFavorite: Boolean = false
) : Parcelable