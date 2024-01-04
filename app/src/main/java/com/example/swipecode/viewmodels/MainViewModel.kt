package com.example.swipecode.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.swipecode.Utils.NetworkResult
import com.example.swipecode.data.Repository
import com.example.swipecode.models.WholeProduct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository, application: Application
) : AndroidViewModel(application) {
    //this live data would notify the data binding setup of any changes
    var productsResponse:MutableLiveData<NetworkResult<WholeProduct>> =MutableLiveData()
    var searchedProductsResponse:MutableLiveData<NetworkResult<WholeProduct>> =MutableLiveData()
    fun getProducts()=viewModelScope.launch {
        getProductsSafely()
    }

    private suspend fun getProductsSafely() {
        // While the rds is fetching data from the API we can set the value to one of the classes of the sealed class we defined earlier.
        productsResponse.value=NetworkResult.Loading()

        if(hasInternetConnection()==true){
            try{
                val response=repository.remote.getProducts()
                productsResponse.value=handleProductResponse(response)

                val products=productsResponse.value!!.data

            }
            catch (e:Exception){
                productsResponse.value=NetworkResult.Error("Products lists could not be found,sorry")
            }
        }
        else{
            productsResponse.value=NetworkResult.Error("No internet connection found buddy")
        }
    }

    private suspend fun searchProductsSafeCall(searchQuery: Map<String, String>) {
        searchedProductsResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.searchProduct(searchQuery)
                searchedProductsResponse.value = handleProductResponse(response)
            } catch (e: Exception) {
                searchedProductsResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            searchedProductsResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun handleProductResponse(response: Response<WholeProduct>): NetworkResult<WholeProduct>? {
        when{
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited.")
            }

            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }

    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}