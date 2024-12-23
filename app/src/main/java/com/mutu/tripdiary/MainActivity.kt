package com.mutu.tripdiary

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mutu.tripdiary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun loginClick(view: View) {
        val emailText = binding.loginEmail.text.toString()
        val passwordText = binding.loginPassword.text.toString()

        val database = this.openOrCreateDatabase("TripDiary", MODE_PRIVATE, null)

        try {
            // Veritabanında kullanıcılar tablosu oluşturuluyor
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS user(id INTEGER PRIMARY KEY, name VARCHAR, surname VARCHAR, username VARCHAR, email VARCHAR, password VARCHAR)"
            )

            // Kullanıcı adı ve şifresine göre sorgu
            val cursor = database.rawQuery(
                "SELECT * FROM user WHERE email = ? AND password = ?",
                arrayOf(emailText, passwordText)
            )

            if (cursor.moveToFirst()) {
                val userIdColumnIndex = cursor.getColumnIndex("id")
                if (userIdColumnIndex != -1) {
                    val userId = cursor.getInt(userIdColumnIndex)
                    println("Kullanıcı ID'si: $userId")

                    // Kullanıcı ID'sini SharedPreferences'a kaydediyoruz
                    val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putInt("userId", userId)  // Kullanıcı ID'sini kaydet
                    editor.apply()

                    // DashboardActivity'ye geçiş yapıyoruz
                    val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@MainActivity, "ID sütunu bulunamadı", Toast.LENGTH_LONG).show()
                }
            } else {
                // Kullanıcı bulunamadı, hata mesajı göster
                Toast.makeText(this@MainActivity, "Eksik veya hatalı bilgi", Toast.LENGTH_LONG).show()
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
