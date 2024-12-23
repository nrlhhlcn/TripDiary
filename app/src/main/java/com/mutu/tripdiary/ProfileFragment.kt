package com.mutu.tripdiary

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class ProfileFragment : Fragment() {

    private var userId: Int = -1
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var userUsernameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(ARG_USER_ID)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // TextView'leri bul
        userNameTextView = view.findViewById(R.id.profile_name)
        userEmailTextView = view.findViewById(R.id.profile_email)
        userUsernameTextView = view.findViewById(R.id.profile_username)

        // Kullanıcı bilgilerini veritabanından al
        getUserDetails()

        return view
    }

    @SuppressLint("Range")
    private fun getUserDetails() {
        val database = activity?.openOrCreateDatabase("TripDiary", MODE_PRIVATE, null)

        try {
            val cursor = database?.rawQuery(
                "SELECT * FROM user WHERE id = ?",
                arrayOf(userId.toString())
            )

            if (cursor?.moveToFirst() == true) {
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val surname = cursor.getString(cursor.getColumnIndex("surname"))
                val username = cursor.getString(cursor.getColumnIndex("username"))
                val email = cursor.getString(cursor.getColumnIndex("email"))

                // Kullanıcı bilgilerini TextView'lerde göster
                userNameTextView.text = "$name $surname"
                userEmailTextView.text = email
                userUsernameTextView.text = username
            } else {

            }

            cursor?.close()
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }

    companion object {
        private const val ARG_USER_ID = "user_id"

        @JvmStatic
        fun newInstance(userId: Int) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_USER_ID, userId)
                }
            }
    }
}
