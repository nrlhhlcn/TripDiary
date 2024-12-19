package com.mutu.tripdiary

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mutu.tripdiary.databinding.ActivityMainBinding
import com.mutu.tripdiary.entities.Users

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }


    fun loginClick(view: View) {
        val emailText = binding.loginEmail.text.toString()
        val passwordText = binding.loginPassword.text.toString()

        val database = this.openOrCreateDatabase("TripDiary", MODE_PRIVATE, null)
        try {

            database.execSQL(
                "CREATE TABLE IF NOT EXISTS user(id INTEGER PRIMARY KEY, name VARCHAR, surname VARCHAR, username VARCHAR, email VARCHAR, password VARCHAR)"
            )

            val cursor = database.rawQuery(
                "SELECT * FROM user WHERE email = ? AND password = ?",
                arrayOf(emailText, passwordText)
            )
            var nameIx = cursor.getColumnIndex("name")
            var surnameIx = cursor.getColumnIndex("surname")
            var usernameIx = cursor.getColumnIndex("username")
            var emailIx = cursor.getColumnIndex("email")
            var passwordIx = cursor.getColumnIndex("password")

            var user: Users? = null
            while (cursor.moveToNext()) {
                user = Users(
                    cursor.getString(nameIx),
                    cursor.getString(surnameIx),
                    cursor.getString(usernameIx),
                    cursor.getString(emailIx),
                    cursor.getString(passwordIx)
                )
            }


            if (cursor.moveToFirst()) {
                println("48")

                if (user != null) {
                    val intent = Intent(this@MainActivity, AnaSayfa::class.java)
                    intent.putExtra("user", user)
                    startActivity(intent)
                }

            } else {

                Toast.makeText(this@MainActivity, "Eksik veya hatalı bilgi", Toast.LENGTH_LONG)
                    .show()
            }


            cursor.close()


        } catch (e: Exception) {
            println("Burda hata ")
            e.printStackTrace()
        }
    }

    fun registerClickLogin(view: View) {
        val intent = Intent(this@MainActivity, RegisterActivity::class.java)
        startActivity(intent)

    }
}