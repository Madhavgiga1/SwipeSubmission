package com.example.swipecode.UI.Fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.swipecode.R
import com.example.swipecode.databinding.FragmentAddProductBinding
import com.example.swipecode.viewmodels.MainViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class AddProductFragment : Fragment() {
    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!
    lateinit var mainViewModel: MainViewModel
    var bitmap: Bitmap? = null
    var imguri:Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddProductBinding.inflate(layoutInflater, container, false)
        binding.capturebutton.setOnClickListener {
            requestCameraGalleryPermission()
        }
        binding.okButton.setOnClickListener {
            var name=binding.productName
            var type=binding.productType
            val tax = binding.productTaxText.text.toString().toDoubleOrNull() ?: 0.0
            val price = binding.productPrice.text.toString().toDoubleOrNull() ?: 0.0
            var imageFile: File?=null
            if(bitmap!=null){
                imageFile=bitmapToFile(bitmap!!)
            }
        }
        return binding.root
    }

    private fun requestCameraGalleryPermission() {
        // If the user denied the permission earlier than show Rational dialog with the text
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showRationalDialog("Swipe Assignment App", "To use this feature you need to allow the access to the gallery")

        }
        else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                imageFromGalleryLauncher.launch(intent)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Gallery permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    fun bitmapToFile(bitmap: Bitmap): File {
        // Assume context is your application or activity context
        val filesDir = context?.filesDir
        val imageFile = File(filesDir, "image.jpg")

        val outputStream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        outputStream.close()

        return imageFile
    }

    private val imageFromGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val selectedImage: Uri? = result.data?.data
                selectedImage?.let { uri ->
                    try {
                        val inputStream = requireActivity().contentResolver.openInputStream(uri)
                        bitmap = BitmapFactory.decodeStream(inputStream)
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                        binding.capturedImageView.setImageBitmap(bitmap)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    private fun showRationalDialog(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", requireActivity().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }
}