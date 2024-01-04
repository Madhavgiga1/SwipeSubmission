package com.example.swipecode.BindingAdapter

import android.util.Log
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import coil.load
import com.example.swipecode.R

class ProductRowBinding {
    companion object {
        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, imageUrl: String?) {
            if (!imageUrl.isNullOrEmpty()) {
                imageView.load(imageUrl) {
                    crossfade(600)

                }
            } else {
                // Handle null or empty imageUrl here (e.g., set a placeholder or default image)
                // For example, setting a placeholder image resource
                imageView.setImageResource(R.drawable.placeholder_image)
            }
        }
    }
}