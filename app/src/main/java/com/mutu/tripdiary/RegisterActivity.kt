package com.mutu.tripdiary

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mutu.tripdiary.databinding.ActivityRegisterBinding
import com.mutu.tripdiary.entities.Users

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    fun registerClick(view: View) {
        val database = this.openOrCreateDatabase("TripDiary", MODE_PRIVATE, null)
        val user = Users(
            binding.name.text.toString(),
            binding.surname.text.toString(),
            binding.username.text.toString(),
            binding.email.text.toString(),
            binding.password.text.toString()
        )

        if (binding.repeatPassword.text.toString() != binding.password.text.toString() ||
            binding.name.text.toString() == "" ||
            binding.surname.text.toString() == "" ||
            binding.password.text.toString() == "" ||
            binding.username.text.toString() == "" ||
            binding.repeatPassword.text.toString() == "" ||
            binding.email.text.toString() == "" ||
            binding.password.text.length < 8


        ) {
            Toast.makeText(this@RegisterActivity, "Eksik veya hatalı bilgi", Toast.LENGTH_LONG)
                .show()

        } else {
            try {
                database.execSQL("CREATE TABLE IF NOT EXISTS user(id INTEGER PRIMARY KEY,name VARCHAR,surname VARCHAR,username VARCHAR,email VARCHAR,password VARCHAR)")

            } catch (e: Exception) {
                println("Burda hata ")
                e.printStackTrace()
            }

            val statement =
                database.compileStatement("INSERT INTO user(name,surname,username,email,password) VALUES (?,?,?,?,?) ")


            statement.bindString(1, user.name)
            statement.bindString(2, user.surname)
            statement.bindString(3, user.usermane)
            statement.bindString(4, user.email)
            statement.bindString(5, user.password)
            statement.executeInsert()
            Toast.makeText(this@RegisterActivity, "Kayıt Başarılı", Toast.LENGTH_LONG).show()
            val intent= Intent(this@RegisterActivity,MainActivity::class.java)
            startActivity(intent)


        }


    }
}