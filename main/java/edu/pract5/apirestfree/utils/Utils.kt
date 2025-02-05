package edu.pract5.apirestfree.utils

import android.content.Context
import android.net.NetworkCapabilities

/**
 * Función checkConnection
 * Comprueba si el dispositivo tiene una conexión de red activa (Wi-Fi, celular o Ethernet).
 * @param context Contexto de la aplicación.
 * @return Boolean indicando si hay una conexión de red activa.
 * @author Jonathan Gómez Fraile
 */
fun checkConnection(context: Context): Boolean {
    val cm =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
    val network = cm.activeNetwork
    if (network != null) {
        val activeNetwork = cm.getNetworkCapabilities(network)
        if (activeNetwork != null) {
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
    }
    return false
}

/**
 * Enum class FilterFoods
 * Define los filtros de comidas a mostrar.
 * - ALL: Mostrar todas las comidas.
 * - FAVORITES: Mostrar solo las comidas favoritas.
 * @autor Jonathan Gómez Fraile
 */
enum class FilterFoods {
    ALL,
    FAVORITES
}

/**
 * Variable filterFoods
 * Define el filtro de comidas a mostrar.
 * - Por defecto, se muestran todas las comidas.
 * @autor Jonathan Gómez Fraile
 */
var filterFoods: FilterFoods = FilterFoods.ALL