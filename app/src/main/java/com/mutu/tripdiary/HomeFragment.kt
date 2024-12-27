package com.mutu.tripdiary

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mutu.tripdiary.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var userId: Int = -1
    private lateinit var tripAdaptor: TripAdaptor
    private var tripList: ArrayList<Trip> = ArrayList()
    private var selectedCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(ARG_USER_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val rootView = binding.root

        val database = requireActivity().openOrCreateDatabase("TripDiary", Context.MODE_PRIVATE, null)
        if (database == null) {
            throw IllegalStateException("Veritabanı açılamadı! Context null olabilir.")
        }

        // Kullanıcı adını al
        val userCursor: Cursor = database.rawQuery(
            "SELECT name FROM user WHERE id = ?",
            arrayOf(userId.toString())
        )
        if (userCursor.moveToFirst()) {
            val userName = userCursor.getString(0)
            binding.welcomeText.text = "Hoşgeldin $userName"
        } else {
            binding.welcomeText.text = "Hoşgeldin Kullanıcı"
        }
        userCursor.close()

        /*
        binding.foodButton.setOnClickListener {
            selectedCategory = "Yemek"
            loadTrips(database, binding)
        }

        binding.natureButton.setOnClickListener {
            selectedCategory = "Doga"
            loadTrips(database, binding)
        }

        binding.entertainmentButton.setOnClickListener {
            selectedCategory = "Eglence"
            loadTrips(database, binding)
        }

        binding.historyButton.setOnClickListener {
            selectedCategory = "Tarih"
            loadTrips(database, binding)
        }

        loadTrips(database, binding) */



        // RecyclerView ve TripAdaptor
        tripAdaptor = TripAdaptor(tripList)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = tripAdaptor

        // Gezi bilgilerini al ve listeye ekle
        val tripCursor: Cursor = database.rawQuery(
            "SELECT tripName, title, description, date, imagePath, tripCategory FROM trip WHERE userId = ? ORDER BY tripId DESC",
            arrayOf(userId.toString())
        )



        val trimNameId = tripCursor.getColumnIndex("tripName")
        val ulkeId = tripCursor.getColumnIndex("title")
        val aniId = tripCursor.getColumnIndex("description")
        val resimId = tripCursor.getColumnIndex("imagePath")
        val dateId = tripCursor.getColumnIndex("date")
        val tripCategory = tripCursor.getColumnIndex("tripCategory")

        // Eğer sonuç yoksa listeyi temizle ve kullanıcıya mesaj göster
        if (!tripCursor.moveToFirst()) {
            binding.welcomeText.text = "Henüz gezi bilginiz yok!"
        } else {
            do {
                val tripName = tripCursor.getString(trimNameId)
                val title = tripCursor.getString(ulkeId)
                val ani = tripCursor.getString(aniId)
                val resim = tripCursor.getString(resimId) ?: ""
                val date = tripCursor.getString(dateId)
                val category = tripCursor.getString(tripCategory)

                // Görsel yolu kontrolü
                val firstImagePath = resim.split(",").getOrElse(0) { "" }

                val trip = Trip(tripName, title, ani, firstImagePath, date, category)
                tripList.add(trip)

            } while (tripCursor.moveToNext())
        }

        /*
       while (tripCursor.moveToNext()) {

           val tripName = tripCursor.getString(trimNameId)
           val title = tripCursor.getString(ulkeId)

           val ani = tripCursor.getString(aniId)
           val resim=tripCursor.getString(resimId)
           val date=tripCursor.getString(dateId)
           val category = tripCursor.getString(tripCategory)

           val firstImagePath = resim.split(",").getOrElse(0) { "" }

           if (firstImagePath.isNotEmpty()) {
               println("İlk görsel yolu: $firstImagePath")
           } else {
               println("Görsel yolu mevcut değil.")
           }


           val  trip=Trip(tripName,title,ani,firstImagePath,date,category)
           println("sadda")
           tripList.add(trip)

       } */
        tripAdaptor.notifyDataSetChanged()
        tripCursor.close()
        database.close()

        return rootView
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
