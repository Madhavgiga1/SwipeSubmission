package com.example.swipecode.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.swipecode.databinding.ProductRowLayoutBinding
import com.example.swipecode.models.WholeProduct
import com.example.swipecode.models.WholeProductItem
import java.util.*

class ProductAdapter(): RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {
    // i will just assign the response of List of WholeProductItems received from the api to the variable products
    var products = mutableListOf<WholeProductItem>()

    class MyViewHolder(private val binding: ProductRowLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(result: WholeProductItem){
            binding.result=result
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup):MyViewHolder{
                val layoutInflater= LayoutInflater.from(parent.context)
                val binding=ProductRowLayoutBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapter.MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductAdapter.MyViewHolder, position: Int) {
        val currentProduct=products[position]
        holder.bind(currentProduct)
    }

    override fun getItemCount(): Int {
        return products.size
    }
}