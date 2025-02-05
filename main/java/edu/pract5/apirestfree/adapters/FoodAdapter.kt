package edu.pract5.apirestfree.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.pract5.apirestfree.R
import edu.pract5.apirestfree.databinding.ItemFoodBinding
import edu.pract5.apirestfree.model.Food

/**
 * Class FoodAdapter.kt
 * Clase que se encarga de manejar la lista de alimentos en la vista
 * @author Jonathan Gómez Fraile
 */
class FoodAdapter(
    private var foodList: List<Food>,
    private val onItemClick: (Food) -> Unit,
    private val onFavoriteClick: (Food) -> Unit
) : ListAdapter<Food, FoodAdapter.FoodViewHolder>(FoodDiffCallback()) {

    /**
     * Función que devuelve el número de elementos de la lista
     */
    override fun getItemCount(): Int = foodList.size

    /**
     * Función que actualiza la lista de alimentos
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<Food>) {
        this.foodList = newList
        notifyDataSetChanged()
    }

    /**
     * Class FoodViewHolder.kt
     * Clase que se encarga de manejar la vista de cada elemento de la lista
     * @param itemView: View vista de cada elemento de la lista
     * @autor Jonathan Gómez Fraile
     */
    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvFoodName: TextView = itemView.findViewById(R.id.tvFoodName)
        private val ivFavorite: ImageView = itemView.findViewById(R.id.ivFavorite)

        fun bind(food: Food) {
            tvFoodName.text = food.name
            fun updateFavoriteIcon() {
                ivFavorite.setImageResource(
                    if (food.isFavorite) R.drawable.favorite_on else R.drawable.favorite_off
                )
            }
            updateFavoriteIcon()
            itemView.setOnClickListener {
                onItemClick(food)
            }

            ivFavorite.setOnClickListener {
                food.isFavorite = !food.isFavorite
                updateFavoriteIcon()
                onFavoriteClick(food)
                notifyItemChanged(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        return FoodViewHolder(
            ItemFoodBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
                .root
        )
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foodList[position])
    }
}

/**
 * Class FoodDiffCallback.kt
 * Clase que se encarga de comparar los elementos de la lista
 * @param oldItem: Food elemento antiguo
 * @param newItem: Food elemento nuevo
 * @return Boolean true si los elementos son iguales, false en caso contrario
 * @autor Jonathan Gómez Fraile
 */
class FoodDiffCallback : DiffUtil.ItemCallback<Food>() {

    override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
        return oldItem == newItem
    }
}
