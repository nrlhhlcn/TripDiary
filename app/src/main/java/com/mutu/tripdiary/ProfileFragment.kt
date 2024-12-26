package com.mutu.tripdiary

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class ProfileFragment : Fragment() {

    private var userId: Int = -1
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var userUsernameTextView: TextView
    private lateinit var logoutButton: Button

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
        // Layout'u inflate et
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Görünüm öğelerini bul
        userNameTextView = view.findViewById(R.id.profile_name)
        userEmailTextView = view.findViewById(R.id.profile_email)
        userUsernameTextView = view.findViewById(R.id.profile_username)
        logoutButton = view.findViewById(R.id.btn_logout)

        // Kullanıcı bilgilerini al
        getUserDetails()

        // Logout butonuna tıklama olayını bağla
        logoutButton.setOnClickListener {
            logout()
        }

        return view
    }

    private fun logout() {
        // SharedPreferences'ten oturumu temizle
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // Tüm verileri sil
        editor.apply()

        // Giriş ekranına yönlendir
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Önceki aktiviteleri temizle
        startActivity(intent)

        // Parent Activity'yi sonlandır
        requireActivity().finish()
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

                // TextView'leri güncelle
                userNameTextView.text = "$name $surname"
                userEmailTextView.text = email
                userUsernameTextView.text = username
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
