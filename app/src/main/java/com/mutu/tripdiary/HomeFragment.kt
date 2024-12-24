package com.mutu.tripdiary

import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import java.io.File

class HomeFragment : Fragment() {

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(ARG_USER_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment layout'unu şişiriyoruz
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        // Kullanıcı ID'sine göre veritabanından kullanıcı adını al
        val database = requireActivity().openOrCreateDatabase("TripDiary", Context.MODE_PRIVATE, null)
        val userCursor: Cursor = database.rawQuery(
            "SELECT name FROM user WHERE id = ?",
            arrayOf(userId.toString())
        )

        val welcomeTextView: TextView = rootView.findViewById(R.id.welcomeText) // Hoşgeldin yazısı için TextView

        if (userCursor.moveToFirst()) {
            val userName = userCursor.getString(0) // Kullanıcı adını al
            welcomeTextView.text = "Hoşgeldin $userName" // Hoşgeldin mesajını ayarla
        } else {
            welcomeTextView.text = "Hoşgeldin Kullanıcı" // Eğer kullanıcı adı bulunamazsa varsayılan mesaj
        }

        userCursor.close() // Cursor'ı kapat

        // Kullanıcı ID'sine göre veritabanından gezi bilgilerini al
        val tripCursor: Cursor = database.rawQuery(
            "SELECT tripName, title, description, date, imagePath FROM trip WHERE userId = ? ORDER BY tripId DESC LIMIT 1",
            arrayOf(userId.toString())
        )

        // Gezi bilgilerini göster
        if (tripCursor.moveToFirst()) {
            val tripName = tripCursor.getString(0)
            val description = tripCursor.getString(2)
            val date = tripCursor.getLong(3)
            val imagePath = tripCursor.getString(4)

            val cardTitle: TextView = rootView.findViewById(R.id.cardTitle)
            val cardDescription: TextView = rootView.findViewById(R.id.cardDescription)
            val cardDate: TextView = rootView.findViewById(R.id.cardDate)
            val cardImage: ImageView = rootView.findViewById(R.id.cardImage)

            cardTitle.text = tripName
            cardDescription.text = description
            cardDate.text = "Tarih: ${java.text.SimpleDateFormat("dd MMMM yyyy").format(date)}"

            val file = File(imagePath)
            if (file.exists()) {
                val imageUri = Uri.fromFile(file)
                Glide.with(requireContext())
                    .load(imageUri)
                    .into(cardImage)
            } else {
                Toast.makeText(context, "Dosya bulunamadı: $imagePath", Toast.LENGTH_SHORT).show()
            }
        }

        tripCursor.close() // Cursor'ı kapat
        database.close() // Veritabanını kapat

        return rootView
    }

    companion object {
        private const val ARG_USER_ID = "user_id"

        @JvmStatic
        fun newInstance(userId: Int) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_USER_ID, userId)
                }
            }
    }
}

