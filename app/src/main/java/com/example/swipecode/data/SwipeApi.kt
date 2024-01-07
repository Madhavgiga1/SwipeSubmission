package com.example.swipecode.data

import com.example.swipecode.models.WholeProduct
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface SwipeApi {

    @GET(value ="get")
    suspend fun getProducts(): Response<WholeProduct>


    @GET("/recipes/complexSearch")
    suspend fun searchProducts(
        @QueryMap searchQuery: Map<String, String>
    ): Response<WholeProduct>

    @Multipart
    @POST("add")
    suspend fun addProduct(
        @Part ("multi")multipartBody: MultipartBody
    ): Response<Unit>
}