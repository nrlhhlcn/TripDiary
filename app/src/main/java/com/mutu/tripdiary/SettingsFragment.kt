package com.mutu.tripdiary

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mutu.tripdiary.databinding.FragmentSettingsBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment() {

    private var userId: Int = -1
    private lateinit var binding: FragmentSettingsBinding
    private val imageFilePaths = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Kullanıcı ID'sini alıyoruz
        arguments?.let {
            userId = it.getInt(ARG_USER_ID)
        }

        // Veritabanı işlemleri (Tabloyu oluşturuyoruz)
        val database = requireActivity().openOrCreateDatabase("TripDiary", Context.MODE_PRIVATE, null)
        database.execSQL(
            """CREATE TABLE IF NOT EXISTS trip(
                tripId INTEGER PRIMARY KEY, 
                userId INTEGER,
                tripName VARCHAR, 
                title VARCHAR, 
                description TEXT, 
                date DATETIME, 
                category VARCHAR,
                countryCity VARCHAR,
                memories TEXT,
                rating REAL,
                imagePaths TEXT,
                FOREIGN KEY(userId) REFERENCES user(id))"""
        )

        // Fotoğraf seçme butonuna tıklama işlemi
        binding.selectImagesButton.setOnClickListener {
            selectImages()
        }

        // Kaydetme butonuna tıklama işlemi
        binding.saveTripButton.setOnClickListener {
            saveTripToDatabase()
        }

        return binding.root
    }

    // Birden fazla fotoğraf seçme işlemi
    private fun selectImages() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    // Seçilen fotoğraflarla işlem yapma
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.let { intent ->
                val clipData = intent.clipData
                if (clipData != null) {
                    // Çoklu fotoğraf seçildiğinde her birini kaydet
                    for (i in 0 until clipData.itemCount) {
                        val imageUri = clipData.getItemAt(i).uri
                        imageFilePaths.add(saveImageToInternalStorage(imageUri))
                    }
                } else {
                    // Tek bir fotoğraf seçildiğinde
                    intent.data?.let { uri ->
                        imageFilePaths.add(saveImageToInternalStorage(uri))
                    }
                }
            }
            Toast.makeText(requireContext(), "Resimler kaydedildi", Toast.LENGTH_SHORT).show()
        }
    }

    // Seçilen resmi internal storage'a kaydetme
    private fun saveImageToInternalStorage(uri: Uri): String {
        return try {
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
            val fileName = "trip_image_${System.currentTimeMillis()}.jpg"
            val file = File(requireContext().filesDir, fileName)

            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }

            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

    // Trip verilerini veritabanına kaydetme
    private fun saveTripToDatabase() {
        val tripName = binding.tripTitleEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val category = binding.categorySpinner.selectedItem.toString()
        val countryCity = binding.countryCityEditText.text.toString()
        val memories = binding.memoriesEditText.text.toString()
        val rating = binding.ratingBar.rating
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val imagePathsString = if (imageFilePaths.isNotEmpty()) {
            imageFilePaths.joinToString(",")
        } else {
            ""
        }

        if (tripName.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_LONG).show()
            return
        }

        val database = requireActivity().openOrCreateDatabase("TripDiary", Context.MODE_PRIVATE, null)

        try {
            val sql = """INSERT INTO trip (userId, tripName, title, description, date, category, countryCity, memories, rating, imagePaths) 
                         VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"""
            val stmt = database.compileStatement(sql)
            stmt.bindLong(1, userId.toLong())
            stmt.bindString(2, tripName)
            stmt.bindString(3, tripName) // "title" olarak tekrar tripName kaydediliyor
            stmt.bindString(4, description)
            stmt.bindString(5, date)
            stmt.bindString(6, category)
            stmt.bindString(7, countryCity)
            stmt.bindString(8, memories)
            stmt.bindDouble(9, rating.toDouble())
            stmt.bindString(10, imagePathsString)
            stmt.executeInsert()

            Toast.makeText(requireContext(), "Gezi kaydedildi", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Bir hata oluştu: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val ARG_USER_ID = "user_id"
        private const val IMAGE_REQUEST_CODE = 1

        @JvmStatic
        fun newInstance(userId: Int) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_USER_ID, userId)
                }
            }
    }
}

