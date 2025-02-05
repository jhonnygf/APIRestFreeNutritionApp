package edu.pract5.apirestfree.ui

import android.app.Activity
import android.app.ActivityOptions
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import edu.pract5.apirestfree.NutritionApp
import edu.pract5.apirestfree.R
import edu.pract5.apirestfree.data.LocalDataSource
import edu.pract5.apirestfree.data.RemoteDataSource
import edu.pract5.apirestfree.data.Repository
import edu.pract5.apirestfree.databinding.ActivityDetailBinding
import edu.pract5.apirestfree.model.Food
import kotlinx.coroutines.launch

/**
 * Class DetailMainActivity
 * Clase que maneja la lógica de la actividad de detalle de un alimento
 * @author Jonathan Gómez Fraile
 */
class DetailMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    private val repository by lazy {
        val db = (application as NutritionApp).nutritionDB
        Repository(
            localDataSource = LocalDataSource(db.foodDao()),
            remoteDataSource = RemoteDataSource()
        )
    }

    companion object {
        const val EXTRA_FOOD = "food"
        fun navigate(activity: Activity, food: Food) {
            val intent = Intent(activity, DetailMainActivity::class.java).apply {
                putExtra(EXTRA_FOOD, food)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            activity.startActivity(
                intent,
                ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
            )
        }
    }

    /**
     * Función que se ejecuta al crear la actividad y muestra la información del alimento
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val food = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra(EXTRA_FOOD, Food::class.java)
        else
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_FOOD)

        Log.d(TAG, "onCreate: $food")

        if (food != null) {
            showFood(food)
            lifecycleScope.launch {
                repository.saveFood(food)
            }
        } else {
            Toast.makeText(this, "No food data", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Función que muestra la información de un alimento en la vista
     */
    private fun showFood(food: Food) {

        val maxFat = 70.0
        val maxSaturatedFat = 20.0
        val maxSodium = 2.2
        val maxPotassium = 3.5
        val maxCholesterol = 0.3
        val maxCarbohydrates = 300.0
        val maxFiber = 25.0
        val maxSugar = 50.0

        //Muestro el nombre del alimento o atributo
        binding.tvName.text = food.name
        binding.tvTotalFat.text = R.string.total_fat.toString()
        binding.tvSaturatedFat.text = R.string.saturated_fat.toString()
        binding.tvSodium.text = R.string.sodium.toString()
        binding.tvPotassium.text = R.string.potassium.toString()
        binding.tvCholesterol.text = R.string.cholesterol.toString()
        binding.tvCarbs.text = R.string.carbohydrates.toString()
        binding.tvFiber.text = R.string.fiber.toString()
        binding.tvSugar.text = R.string.sugar.toString()

        //Cargo la información de los nutrientes en las progress bars
        binding.pbTotalFat.progress = calculatePercentage(food.fatTotalG, maxFat)
        binding.pbSaturatedFat.progress = calculatePercentage(food.fatSaturatedG, maxSaturatedFat)
        binding.pbSodium.progress = calculatePercentage(food.sodiumMg, maxSodium)
        binding.pbPotassium.progress = calculatePercentage(food.potassiumMg, maxPotassium)
        binding.pbCholesterol.progress = calculatePercentage(food.cholesterolMg, maxCholesterol)
        binding.pbCarbs.progress = calculatePercentage(food.carbohydratesTotalG, maxCarbohydrates)
        binding.pbFiber.progress = calculatePercentage(food.fiberG, maxFiber)
        binding.pbSugar.progress = calculatePercentage(food.sugarG, maxSugar)

        //Cargo la información de los nutrientes en los text views
        binding.tvTotalFatValues.text = formatNutrientValue(food.fatTotalG, maxFat)
        binding.tvSaturatedFatValues.text = formatNutrientValue(food.fatSaturatedG, maxSaturatedFat)
        binding.tvSodiumValues.text = formatNutrientValue(food.sodiumMg, maxSodium)
        binding.tvPotassiumValues.text = formatNutrientValue(food.potassiumMg, maxPotassium)
        binding.tvCholesterolValues.text = formatNutrientValue(food.cholesterolMg, maxCholesterol)
        binding.tvCarbsValues.text = formatNutrientValue(food.carbohydratesTotalG, maxCarbohydrates)
        binding.tvFiberValues.text = formatNutrientValue(food.fiberG, maxFiber)
        binding.tvSugarValues.text = formatNutrientValue(food.sugarG, maxSugar)

    }

    /**
     * Función que calcula el porcentaje de un valor en relación a un máximo recomendado diario
     */
    private fun calculatePercentage(value: Double?, max: Double): Int {
        if (value == null || max == 0.0) return 0
        return ((value / max) * 100).toInt().coerceIn(0, 100)
    }

    /**
     * Función que formatea el valor de un nutriente para mostrarlo en la vista
     */
    private fun formatNutrientValue(foodValue: Double?, maxValue: Any, unit: String = " g"): String {
        return "$foodValue / $maxValue$unit"
    }

}