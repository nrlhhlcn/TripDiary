package com.mutu.tripdiary

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
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

        // Kullanıcı ID'sine göre veritabanından gezi bilgilerini alıyoruz
        val database = requireActivity().openOrCreateDatabase("TripDiary", Context.MODE_PRIVATE, null)
        val cursor: Cursor = database.rawQuery(
            "SELECT tripName, title, description, date, imagePath FROM trip WHERE userId = ? LIMIT 1",
            arrayOf(userId.toString())
        )

        // İlk geziyi sorgulayıp ekrana yazdırıyoruz
        if (cursor.moveToFirst()) {
            val tripName = cursor.getString(0)
            val title = cursor.getString(1)
            val description = cursor.getString(2)
            val date = cursor.getLong(3) // Tarihi alıyoruz
            val imagePath = cursor.getString(4)

            // CardView içindeki bileşenleri buluyoruz
            val cardTitle: TextView = rootView.findViewById(R.id.cardTitle)
            val cardDescription: TextView = rootView.findViewById(R.id.cardDescription)
            val cardDate: TextView = rootView.findViewById(R.id.cardDate)
            val cardImage: ImageView = rootView.findViewById(R.id.cardImage)

            // Bileşenlere sorgulanan verileri yazıyoruz
            cardTitle.text = tripName // Gezi adı
            cardDescription.text = description // Gezi açıklaması
            cardDate.text = "Tarih: ${java.text.SimpleDateFormat("dd MMMM yyyy").format(date)}" // Tarihi formatlıyoruz

            // Fotoğrafı gösteriyoruz (imagePath varsa)
            val file = File(imagePath)
            if (file.exists()) {
                // Glide ile yükleme
                Glide.with(requireContext())
                    .load(file)
                    .into(cardImage)
            } else {
                Toast.makeText(context, "Dosya bulunamadı: $imagePath", Toast.LENGTH_SHORT).show()
            }



        }

        cursor.close() // Cursor'ı kapatıyoruz
        database.close() // Veritabanını kapatıyoruz

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
