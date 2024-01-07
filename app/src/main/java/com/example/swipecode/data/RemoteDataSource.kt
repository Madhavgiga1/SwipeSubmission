package com.example.swipecode.data

import com.example.swipecode.models.WholeProduct
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val swipeApi: SwipeApi) {

    suspend fun getProducts(): Response<WholeProduct> {
        return swipeApi.getProducts()
    }

    suspend fun searchProduct(searchQuery: Map<String, String>): Response<WholeProduct> {
        return swipeApi.searchProducts(searchQuery)
    }

    suspend fun addProduct(
        multipartBody: MultipartBody
    ): Response<Unit>{


        return swipeApi.addProduct(
            multipartBody
        )
    }



}