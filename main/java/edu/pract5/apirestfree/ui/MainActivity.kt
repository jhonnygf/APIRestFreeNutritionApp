package edu.pract5.apirestfree.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import edu.pract5.apirestfree.NutritionApp
import edu.pract5.apirestfree.R
import edu.pract5.apirestfree.adapters.FoodAdapter
import edu.pract5.apirestfree.data.LocalDataSource
import edu.pract5.apirestfree.data.RemoteDataSource
import edu.pract5.apirestfree.data.Repository
import edu.pract5.apirestfree.databinding.ActivityMainBinding
import edu.pract5.apirestfree.utils.FilterFoods
import edu.pract5.apirestfree.utils.checkConnection
import edu.pract5.apirestfree.utils.filterFoods
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * Class MainActivity.kt
 * Clase principal de la aplicación que muestra la lista de alimentos, en ella se pueden ir
 * añadiendo alimentos que haya buscado el usuario en la API a través del buscador. Cada
 * búsqueda quedará guardada en la base de datos local para que el usuario pueda acceder a ella
 * sin necesidad de conexión a internet. El usuario se puede crear una lista de alimentos favoritos,
 * visualizar el contenido de cada atributo que contiene ese alimento, podra ordenar alfabeticamente.
 */
@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isAscending: Boolean = true

    private val vm: MainViewModel by viewModels {
        val db = (application as NutritionApp).nutritionDB.foodDao()
        val remoteDataSource = RemoteDataSource()
        val localDataSource = LocalDataSource(db)
        val repository = Repository(remoteDataSource, localDataSource)
        MainViewModelFactory(repository)
    }

    private val adapter = FoodAdapter(
        emptyList(),
        onItemClick = { selectedFood ->
            DetailMainActivity.navigate(this, selectedFood)
        },
        onFavoriteClick = { selectedFood ->
            vm.updateFood(selectedFood)
        }
    )

    /**
     * Función que se ejecuta al crear la actividad y muestra la lista de alimentos
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (!checkConnection(this)) {
            Toast.makeText(this, R.string.txt_no_connexion, Toast.LENGTH_SHORT).show()
        }
        setSupportActionBar(binding.mToolbar)

        binding.mRecycler.setHasFixedSize(true)
        binding.mRecycler.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.foods.collect { newList ->
                    if (newList.isNotEmpty()) {
                        binding.tvNoInfo.visibility = View.GONE
                        adapter.updateData(newList)
                    } else {
                        binding.tvNoInfo.visibility = View.VISIBLE
                        adapter.updateData(emptyList())
                    }
                }
            }
        }
    }

    /**
     * Función que se ejecuta al iniciar la actividad y carga el menu de opciones
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)
        return true
    }

    /**
     * Función que se ejecuta al seleccionar una opción del menú
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.opt_sort -> {
                sortFoodList()
                true
            }

            R.id.opt_about -> {
                showAbout()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Función que se ejecuta al iniciar la actividad
     */
    override fun onStart() {
        super.onStart()

        if (checkConnection(this)) {
            lifecycleScope.launch {
                vm.loadFilteredFoods()
            }
        } else {
            Toast.makeText(this, R.string.txt_no_connexion, Toast.LENGTH_SHORT).show()
        }

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            if (!checkConnection(this)) {
                Toast.makeText(this, R.string.txt_no_connexion, Toast.LENGTH_SHORT).show()
                return@setOnItemSelectedListener false
            }
            when (menuItem.itemId) {
                R.id.opt_all -> {
                    filterFoods = FilterFoods.ALL
                    lifecycleScope.launch {
                        vm.loadFilteredFoods()
                    }
                    true
                }

                R.id.opt_favorite -> {
                    filterFoods = FilterFoods.FAVORITES
                    lifecycleScope.launch {
                        vm.loadFilteredFoods()
                    }
                    true
                }

                else -> false
            }
        }

        binding.mRecycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = adapter
        }

        lifecycleScope.launchWhenStarted {
            vm.foods.collect { newList ->
                if (newList.isNotEmpty()) {
                    binding.tvNoInfo.visibility = View.GONE
                    adapter.updateData(newList)
                } else {
                    binding.tvNoInfo.visibility = View.VISIBLE
                    adapter.updateData(emptyList())
                }
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (checkConnection(this@MainActivity)) {
                    query?.let { vm.searchFood(it) }
                } else {
                    Toast.makeText(this@MainActivity, R.string.txt_no_connexion, Toast.LENGTH_SHORT)
                        .show()
                }
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })

    }

    /**
     * Función que ordena la lista de alimentos por nombre
     */
    private fun sortFoodList() {
        lifecycleScope.launch {
            val currentList = vm.foods.firstOrNull() ?: emptyList()
            val sortedList = if (isAscending) {
                currentList.sortedByDescending { it.name }
            } else {
                currentList.sortedBy { it.name }
            }
            isAscending = !isAscending
            adapter.updateData(sortedList)

            val order = if (isAscending) "Ascendente" else "Descendente"
            Toast.makeText(this@MainActivity, "Ordenado $order", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Función que muestra un diálogo con información acerca del autor de la aplicación
     */
    private fun showAbout() {
        AlertDialog.Builder(this)
            .setTitle("Acerca de")
            .setMessage("Autor: Jonathan Gómez Fraile\nCurso: 2º DAM\nAño: 2024-2025")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}