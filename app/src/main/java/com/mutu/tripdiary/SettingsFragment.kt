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

    private val imageFilePaths = mutableListOf<String>()

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
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Birden fazla fotoğraf seçmeye izin ver
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    // Seçilen fotoğrafın içeriğiyle işlem yapma
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
    private fun saveImageToInternalStorage(uri: Uri): String {
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
            ""
        }
    }

    // Seçilen resmi internal storage'a kaydetme


    // Trip verilerini veritabanına kaydetme
    private fun saveTripToDatabase() {
        val title = binding.tripTitleEditText.text.toString()
        val description = binding.tripDescriptionEditText.text.toString()
        val tripName = binding.tripNameEditText.text.toString()
        val currentDate = binding.tripDateEditText.text.toString()


        val database = requireActivity().openOrCreateDatabase("TripDiary", Context.MODE_PRIVATE, null)

        try {
            val imagePathsString = imageFilePaths.joinToString(",") // Fotoğraf yollarını virgülle ayır

            val sql = """INSERT INTO trip (userId, tripName, title, description, imagePaths,date) 
                         VALUES (?, ?, ?, ?, ?, ?)"""
            val stmt = database.compileStatement(sql)
            stmt.bindLong(1, userId.toLong())
            stmt.bindString(2, tripName)
            stmt.bindString(3, title)
            stmt.bindString(4, description)
            stmt.bindString(6, currentDate)
            stmt.bindString(5, imagePathsString) // Fotoğraf yollarını kaydediyoruz
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