package com.example.swipecode.data

import com.example.swipecode.models.WholeProduct
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val swipeApi: SwipeApi) {

    suspend fun getRecipes(queries: Map<String,String>): Response<WholeProduct> {
        return swipeApi.getProducts(queries)
    }
}