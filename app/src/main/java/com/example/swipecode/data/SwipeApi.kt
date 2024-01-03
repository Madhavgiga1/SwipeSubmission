package com.example.swipecode.data

import com.example.swipecode.models.WholeProduct
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface SwipeApi {

    @GET(value = "/recipes/complexSearch")
    suspend fun getProducts(@QueryMap queries:Map<String,String>): Response<WholeProduct>
}