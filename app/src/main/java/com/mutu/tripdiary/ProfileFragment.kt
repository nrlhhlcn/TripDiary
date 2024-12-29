package com.mutu.tripdiary

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class ProfileFragment : Fragment() {

    private var userId: Int = -1
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var userUsernameEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var changePasswordButton: Button
    private lateinit var updateButton: Button
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
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        userNameTextView = view.findViewById(R.id.profile_name)
        userEmailTextView = view.findViewById(R.id.profile_email)
        userUsernameEditText = view.findViewById(R.id.profile_username)
        newPasswordEditText = view.findViewById(R.id.profile_new_password)
        changePasswordButton = view.findViewById(R.id.btn_change_password)
        updateButton = view.findViewById(R.id.btn_update)
        logoutButton = view.findViewById(R.id.btn_logout)

        getUserDetails()

        // Şifre Güncelleme
        changePasswordButton.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString()
            if (newPassword.isNotEmpty()) {
                changePassword(newPassword)
            } else {
                Toast.makeText(requireContext(), "Şifre boş olamaz!", Toast.LENGTH_SHORT).show()
            }
        }

        // Kullanıcı Adı ve Diğer Bilgileri Güncelleme
        updateButton.setOnClickListener {
            val newUsername = userUsernameEditText.text.toString()
            if (newUsername.isNotEmpty()) {
                updateUserDetails(newUsername)
            } else {
                Toast.makeText(requireContext(), "Kullanıcı adı boş olamaz!", Toast.LENGTH_SHORT).show()
            }
        }

        // Çıkış Yap
        logoutButton.setOnClickListener {
            logout()
        }

        return view
    }

    private fun changePassword(newPassword: String) {
        val database = activity?.openOrCreateDatabase("TripDiary", MODE_PRIVATE, null)

        try {
            database?.execSQL(
                "UPDATE user SET password = ? WHERE id = ?",
                arrayOf(newPassword, userId.toString())
            )
            Toast.makeText(requireContext(), "Şifre başarıyla güncellendi!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Şifre güncellenirken hata oluştu!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserDetails(newUsername: String) {
        val database = activity?.openOrCreateDatabase("TripDiary", MODE_PRIVATE, null)

        try {
            database?.execSQL(
                "UPDATE user SET username = ? WHERE id = ?",
                arrayOf(newUsername, userId.toString())
            )
            Toast.makeText(requireContext(), "Bilgiler başarıyla güncellendi!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Bilgiler güncellenirken hata oluştu!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

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

                userNameTextView.text = "$name $surname"
                userEmailTextView.text = email
                userUsernameEditText.setText(username)
            }
            cursor?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val ARG_USER_ID = "userId"

        @JvmStatic
        fun newInstance(userId: Int) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_USER_ID, userId)
                }
            }
    }
}
