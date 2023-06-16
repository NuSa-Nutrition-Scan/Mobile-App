package com.dicoding.picodiploma.nusa_nutritionscan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.nusa_nutritionscan.Activity.Top15Item

class FoodAdapter(private val listFood: List<Top15Item>) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val foodText: TextView = view.findViewById(R.id.food_name)

        fun bind(food: Top15Item){
            foodText.text = food.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false))

    override fun getItemCount(): Int = listFood.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val food = listFood[position]
        holder.bind(food)
    }
}