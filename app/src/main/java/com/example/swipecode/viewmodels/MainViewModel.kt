package com.example.swipecode.viewmodels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.swipecode.Utils.NetworkResult
import com.example.swipecode.data.Repository
import com.example.swipecode.models.ImageResponse
import com.example.swipecode.models.WholeProduct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.lang.StringBuilder
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository, application: Application
) : AndroidViewModel(application) {

    var productsResponse:MutableLiveData<NetworkResult<WholeProduct>> =MutableLiveData()
    var searchedProductsResponse:MutableLiveData<NetworkResult<WholeProduct>> =MutableLiveData()
    var uploadProductsResponse:MutableLiveData<NetworkResult<ImageResponse>> =MutableLiveData()

    //for getting list of products
    fun getProducts()=viewModelScope.launch {
        getProductsSafely()
    }


    private suspend fun getProductsSafely() {
        // While the rds is fetching data from the API we can set the value to one of the classes of the sealed class we defined earlier.
        productsResponse.value=NetworkResult.Loading()

        if(hasInternetConnection()){
            try{
                val response=repository.remote.getProducts()
                productsResponse.value=handleProductResponse(response)

            }
            catch (e:Exception){
                productsResponse.value=NetworkResult.Error("Products lists could not be found,sorry")
            }
        }
        else{
            productsResponse.value=NetworkResult.Error("No internet connection found buddy")
        }
    }

    private fun handleProductResponse(response: Response<WholeProduct>): NetworkResult<WholeProduct>? {
        when{
            response.isSuccessful -> {
                val productres = response.body()
                return NetworkResult.Success(productres!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }

    }

    //for searching pridcts
    fun searchProducts(searchQuery: Map<String, String>)=viewModelScope.launch {
        searchProductsSafeCall(searchQuery)
    }
    private suspend fun searchProductsSafeCall(searchQuery: Map<String, String>) {
        searchedProductsResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.searchProduct(searchQuery)
                searchedProductsResponse.value = handleProductResponse(response)
            } catch (e: Exception) {
                searchedProductsResponse.value = NetworkResult.Error("Products not found.")
            }
        } else {
            searchedProductsResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }




    //for uploading a custom product
    fun uploadProducts(
        productName: String,
        productType: String,
        price: String,
        tax: String,
        imageFile: File?=null
    )=viewModelScope.launch {
        uploadProductsSafely(productName, productType, price, tax, imageFile)
    }

    private suspend fun uploadProductsSafely(
        productName: String,
        productType: String,
        price: String,
        tax: String,
        imageFile: File?=null
    ) {
        if (hasInternetConnection()) {
            uploadProductsResponse.value=NetworkResult.Loading()
            try {
                var multipartBody=makereqbody(productName, productType, price, tax, imageFile)
                val response = repository.remote.addProduct(multipartBody)
                uploadProductsResponse.value=handleUploadResponse(response)

            }
            catch (e: Exception) {
                // Exception during upload

                Toast.makeText(
                    getApplication<Application>().applicationContext,
                    "Error uploading image: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // No internet connection
            Toast.makeText(
                getApplication<Application>().applicationContext,
                "Sorry, no internet",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleUploadResponse(response: Response<ImageResponse>): NetworkResult<ImageResponse>? {
        when{
            response.isSuccessful -> {
                val productres = response.body()
                return NetworkResult.Success(productres!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun prepareImagePart(imageFile: File): MultipartBody.Part {
        val imageRequestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)
    }
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }


    fun convertBitmapToFile(bitmap: Bitmap): File {
        val filesDir = getApplication<Application>().filesDir
        val imageFile = File(filesDir, "image.jpg")

        val outputStream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        outputStream.close()

        return imageFile
    }
    private fun makereqbody(
        productName: String,
        productType: String,
        price: String,
        tax: String,
        imageFile: File?
    ): MultipartBody {
        var name = productName.toRequestBody()
        var type = productType.toRequestBody()
        var price = price.toRequestBody()
        var tax = tax.toRequestBody()
        var imagepart: MultipartBody.Part? = null
        if (imageFile != null) {
            imagepart = prepareImagePart(imageFile)
        }
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        builder.addPart(name)
        builder.addPart(type)
        if (imagepart != null) {
            builder.addPart(imagepart)
        }
        builder.addPart(price)
        builder.addPart(tax)
        return builder.build()

    }
}