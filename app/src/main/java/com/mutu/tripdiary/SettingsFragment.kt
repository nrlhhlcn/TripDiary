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

class SettingsFragment : Fragment() {

    private var userId: Int = -1
    private lateinit var binding: FragmentSettingsBinding
    private var imageFilePath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Kullanıcı ID'sini alıyoruz
        arguments?.let {
            userId = it.getInt(ARG_USER_ID)
        }

        // Veritabanı işlemleri (Tabloyu oluşturuyoruz)
        val database = requireActivity().openOrCreateDatabase("TripDiary", Context.MODE_PRIVATE, null)

        // Trip tablosu oluşturuluyor
        database.execSQL(
            """CREATE TABLE IF NOT EXISTS trip(
                tripId INTEGER PRIMARY KEY, 
                userId INTEGER,
                tripName VARCHAR, 
                title VARCHAR, 
                description TEXT, 
                date DATETIME, 
                imagePath VARCHAR,
                FOREIGN KEY(userId) REFERENCES user(id))"""
        )

        // Fotoğraf seçme butonuna tıklama işlemi
        binding.selectImageButton.setOnClickListener {
            selectImage()
        }

        // Kaydetme butonuna tıklama işlemi
        binding.saveTripButton.setOnClickListener {
            saveTripToDatabase()
        }

        return binding.root
    }

    // Fotoğraf seçme işlemi
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    // Seçilen fotoğrafın içeriğiyle işlem yapma
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            val selectedImageUri = data?.data
            selectedImageUri?.let {
                imageFilePath = saveImageToInternalStorage(it)
                Toast.makeText(requireContext(), "Resim kaydedildi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Seçilen resmi internal storage'a kaydetme
    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
            val fileName = "trip_image_${System.currentTimeMillis()}.jpg"
            val file = File(requireContext().filesDir, fileName)

            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    // Trip verilerini veritabanına kaydetme
    private fun saveTripToDatabase() {
        val title = binding.tripTitleEditText.text.toString()
        val description = binding.tripDescriptionEditText.text.toString()
        val tripName = binding.tripNameEditText.text.toString()
        val currentDate = System.currentTimeMillis()

        val database = requireActivity().openOrCreateDatabase("TripDiary", Context.MODE_PRIVATE, null)

        try {
            val sql = """INSERT INTO trip (userId, tripName, title, description, date, imagePath) 
                         VALUES (?, ?, ?, ?, ?, ?)"""
            val stmt = database.compileStatement(sql)
            stmt.bindLong(1, userId.toLong()) // userId'yi burada kullanıyoruz
            stmt.bindString(2, tripName)
            stmt.bindString(3, title)
            stmt.bindString(4, description)
            stmt.bindLong(5, currentDate) // Tarihi long olarak kaydediyoruz
            stmt.bindString(6, imageFilePath ?: "") // Fotoğraf yolunu kaydediyoruz
            stmt.executeInsert()

            Toast.makeText(requireContext(), "Trip kaydedildi", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Bir hata oluştu", Toast.LENGTH_LONG).show()
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