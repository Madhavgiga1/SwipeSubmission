package com.example.swipecode.data

import com.example.swipecode.models.WholeProduct
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val swipeApi: SwipeApi) {

    suspend fun getProducts(): Response<WholeProduct> {
        return swipeApi.getProducts()
    }

    suspend fun searchProduct(searchQuery: Map<String, String>): Response<WholeProduct> {
        return swipeApi.searchProducts(searchQuery)
    }

    suspend fun addProduct(
        productName: String,
        productType: String,
        price: Double,
        tax: Double,
        imageFile: File?
    ): Response<Unit> {
        val imagePart = prepareImagePart(imageFile!!)

        return swipeApi.addProduct(
            productName.toRequestBody(),
            productType.toRequestBody(),
            price.toString().toRequestBody(),
            tax.toString().toRequestBody(),
            imagePart
        )
    }

    private fun prepareImagePart(imageFile: File): MultipartBody.Part {
        val imageRequestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)
    }

}