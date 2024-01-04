package com.example.swipecode.models


import com.google.gson.annotations.SerializedName

data class WholeProductItem(
    @SerializedName("image")
    val image: String?,
    @SerializedName("price")
    val price: Double,
    @SerializedName("product_name")
    val productName: String?,
    @SerializedName("product_type")
    val productType: String?,
    @SerializedName("tax")
    val tax: Double
)
