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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        // Access the database
        val database = requireActivity().openOrCreateDatabase("TripDiary", Context.MODE_PRIVATE, null)

        // Query to get the user's name
        val userCursor: Cursor = database.rawQuery(
            "SELECT name FROM user WHERE id = ?",
            arrayOf(userId.toString())
        )

        val welcomeTextView: TextView = rootView.findViewById(R.id.welcomeText)

        if (userCursor.moveToFirst()) {
            val userName = userCursor.getString(0)
            welcomeTextView.text = "Hoşgeldin $userName"
        } else {
            welcomeTextView.text = "Hoşgeldin Kullanıcı"
        }

        userCursor.close()

        // Query to get all trips for the user
        val tripCursor: Cursor = database.rawQuery(
            "SELECT tripName, title, description, date, imagePaths FROM trip WHERE userId = ? ORDER BY tripId DESC",
            arrayOf(userId.toString())
        )

        // Create a list to hold trip data
        val tripList = mutableListOf<Trip>()

        while (tripCursor.moveToNext()) {
            val tripName = tripCursor.getString(0)
            val description = tripCursor.getString(2) ?: "Açıklama yok"
            val date = tripCursor.getLong(3)
            val imagePath = tripCursor.getString(4)

            // Add each trip to the list
            tripList.add(Trip(tripName, description, date, imagePath))
        }

        // Set up RecyclerView with the adapter
        val tripRecyclerView: RecyclerView = rootView.findViewById(R.id.tripRecyclerView)
        tripRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        tripRecyclerView.adapter = TripAdapter(tripList)

        tripCursor.close()
        database.close()

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

data class Trip(
    val tripName: String,
    val description: String,
    val date: Long,
    val imagePath: String
)

class TripAdapter(private val tripList: List<Trip>) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = tripList[position]
        holder.bind(trip)
    }

    override fun getItemCount(): Int = tripList.size


    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.tripImage)
        private val tripNameTextView: TextView = itemView.findViewById(R.id.tripNameTextView)

        fun bind(trip: Trip) {
            // Set the trip name
            tripNameTextView.text = trip.tripName

            // Load the image
            val file = File(trip.imagePath)
            if (file.exists()) {
                val imageUri = Uri.fromFile(file)
                Glide.with(itemView.context)
                    .load(imageUri)
                    .into(imageView)
            } else {
                Toast.makeText(itemView.context, "Dosya bulunamadı: ${trip.imagePath}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}



