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


        binding.foodButton.setOnClickListener {
            selectedCategory = "Yemek"
            loadTrips(database, binding)
        }

        binding.natureButton.setOnClickListener {
            selectedCategory = "Doğa"
            loadTrips(database, binding)
        }

        binding.entertainmentButton.setOnClickListener {
            selectedCategory = "Eğlence"
            loadTrips(database, binding)
        }

        binding.historyButton.setOnClickListener {
            selectedCategory = "Tarihi"
            loadTrips(database, binding)
        }

        binding.showAllButton.setOnClickListener {
            selectedCategory = null // Tüm tripleri göstermek için null yap
            loadTrips(database, binding)
        }

        tripAdaptor = TripAdaptor(tripList)

        loadTrips(database, binding)



        /*
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
        } */


        return rootView
    }

    private fun loadTrips(database: SQLiteDatabase, binding: FragmentHomeBinding) {
        tripList.clear()

        val query = if (selectedCategory.isNullOrEmpty()) {
            "SELECT * FROM trip WHERE userId = ? ORDER BY tripId DESC"
        } else {
            "SELECT * FROM trip WHERE tripCategory = ? AND userId = ? ORDER BY tripId DESC"
        }

        val tripCursor = if (selectedCategory.isNullOrEmpty()) {
            database.rawQuery(query, arrayOf(userId.toString()))  // Tek arrayOf kullanılıyor
        } else {
            database.rawQuery(query, arrayOf(selectedCategory, userId.toString()))  // İki parametreyi tek arrayOf ile birleştiriyoruz
        }

        val tripName = tripCursor.getColumnIndex("tripName")
        val title = tripCursor.getColumnIndex("title")
        val description = tripCursor.getColumnIndex("description")
        val date = tripCursor.getColumnIndex("date")
        val imagePath = tripCursor.getColumnIndex("imagePath")
        val tripCategory = tripCursor.getColumnIndex("tripCategory")


        if (tripCursor.moveToFirst()) {
            do {
                val tripName = tripCursor.getString(tripName)
                val title = tripCursor.getString(title)
                val description = tripCursor.getString(description)
                val resim = tripCursor.getString(imagePath) ?: ""
                val date = tripCursor.getString(date)
                val category = tripCursor.getString(tripCategory)

                val firstImagePath = resim.split(",").getOrElse(0) { "" }
                val trip = Trip(tripName, title, description, firstImagePath, date, category)
                tripList.add(trip)

            } while (tripCursor.moveToNext())
        }

        tripCursor.close()
        tripAdaptor = TripAdaptor(tripList)
        binding.recyclerView.adapter = tripAdaptor
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        tripAdaptor.notifyDataSetChanged()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        val database = requireActivity().openOrCreateDatabase("TripDiary", Context.MODE_PRIVATE, null)
        database.close()

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
