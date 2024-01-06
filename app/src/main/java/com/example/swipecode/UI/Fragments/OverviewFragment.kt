package com.example.swipecode.UI.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swipecode.R
import com.example.swipecode.Utils.NetworkResult
import com.example.swipecode.viewmodels.MainViewModel
import com.example.swipecode.adapters.ProductAdapter
import com.example.swipecode.databinding.FragmentOverviewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class OverviewFragment : Fragment() {
    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private val mAdapter by lazy { ProductAdapter() }
    private lateinit var mView:View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requestApiData()
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        binding.viewModel = mainViewModel!!

        setupRecyclerView()
        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_overviewFragment_to_addProductFragment)
        }

        return binding.root
    }
    private fun setupRecyclerView() {
        binding.recyclerview.adapter = mAdapter
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())

    }
    /*override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchApiData(query)
        }
        return true
    }

    private fun searchApiData(query: String) {

    }*/

    private fun requestApiData() {
        mainViewModel.getProducts()
        mainViewModel.productsResponse.observe(viewLifecycleOwner,{ response ->
            when(response){
                is NetworkResult.Success -> {

                    response.data?.let {
                        val productList = ArrayList(it) // Convert to ArrayList
                        mAdapter.products = productList
                        setupRecyclerView()

                    }

                }
                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}