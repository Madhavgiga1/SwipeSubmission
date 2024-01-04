package com.example.swipecode.data

import com.example.swipecode.models.WholeProduct
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface SwipeApi {

    @GET(value = "/get")
    suspend fun getProducts(): Response<WholeProduct>


    @GET("/recipes/complexSearch")
    suspend fun searchProducts(
        @QueryMap searchQuery: Map<String, String>
    ): Response<WholeProduct>
}