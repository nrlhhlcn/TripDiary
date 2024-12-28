package com.mutu.tripdiary

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.mutu.tripdiary.databinding.FragmentSettingsBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SettingsFragment : Fragment() {

    private var userId: Int = -1
    private lateinit var binding: FragmentSettingsBinding
    private val imageFilePaths = mutableListOf<String>()

    // Çoklu resim seçimi için ActivityResultLauncher
    private val selectImagesLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                uris.forEach { uri ->
                    imageFilePaths.add(saveImageToInternalStorage(uri))
                }
                Toast.makeText(requireContext(), "Resimler kaydedildi", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Kullanıcı ID'sini alıyoruz
        arguments?.let {
            userId = it.getInt(ARG_USER_ID)
        }

        val spinner = binding.tripCategorySpinner
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.trip_categories,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

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
                imagePath VARCHAR,
                tripCategory VARCHAR,
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

        // Silme butonuna tıklama işlemi

        // Tarih seçici açma
        binding.tripDateEditText.setOnClickListener {
            openDatePicker()
        }

        return binding.root
    }

    // Çoklu resim seçme işlemi
    private fun selectImage() {
        selectImagesLauncher.launch("image/*")
    }

    // Seçilen resimleri kaydetme işlemi
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

    // Trip verilerini veritabanına kaydetme
    private fun saveTripToDatabase() {
        val title = binding.tripTitleEditText.text.toString()
        val description = binding.tripDescriptionEditText.text.toString()
        val tripName = binding.tripNameEditText.text.toString()
        val currentDate = binding.tripDateEditText.text.toString()
        val tripCategory = binding.tripCategorySpinner.selectedItem.toString()

        // Boş alan kontrolü
        if (tripName.isEmpty() || title.isEmpty() || description.isEmpty() || currentDate.isEmpty()) {
            Toast.makeText(requireContext(), "Tüm alanları doldurunuz", Toast.LENGTH_SHORT).show()
            return
        }

        // Açıklama için minimum karakter kontrolü
        if (description.length < 100) {
            Toast.makeText(requireContext(), "Açıklama en az 100 karakter olmalıdır", Toast.LENGTH_SHORT).show()
            return
        }

        val database = requireActivity().openOrCreateDatabase("TripDiary", Context.MODE_PRIVATE, null)
        database.beginTransaction()
        try {
            val imagePathsString = imageFilePaths.joinToString(",") // Fotoğraf yollarını virgülle ayır

            val sql = """INSERT INTO trip (userId, tripName, title, description, imagePath, date, tripCategory) 
                     VALUES (?, ?, ?, ?, ?, ?, ?)"""
            val stmt = database.compileStatement(sql)
            stmt.bindLong(1, userId.toLong())
            stmt.bindString(2, tripName)
            stmt.bindString(3, title)
            stmt.bindString(4, description)
            stmt.bindString(5, imagePathsString) // Fotoğraf yollarını kaydediyoruz
            stmt.bindString(6, currentDate)
            stmt.bindString(7, tripCategory)
            stmt.executeInsert()

            database.setTransactionSuccessful()
            Toast.makeText(requireContext(), "Trip kaydedildi", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Bir hata oluştu", Toast.LENGTH_LONG).show()
        } finally {
            database.endTransaction()
        }
    }

    // Tarih seçici açma işlemi
    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val date = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                binding.tripDateEditText.text = date // Seçilen tarihi TextView'e yazdır
            },
            year, month, day
        )
        datePickerDialog.show()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tripDateEdit.setOnClickListener {
            openDatePicker()
        }
    }

    // tripCategory null olan verileri silme


    companion object {
        private const val ARG_USER_ID = "user_id"

        @JvmStatic
        fun newInstance(userId: Int) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_USER_ID, userId)
                }
            }
    }
}
