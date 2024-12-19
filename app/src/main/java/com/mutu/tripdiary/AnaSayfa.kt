package com.mutu.tripdiary

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mutu.tripdiary.databinding.ActivityAnaSayfaBinding
import com.mutu.tripdiary.entities.Users

class AnaSayfa : AppCompatActivity() {
    private lateinit var  binding:ActivityAnaSayfaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      binding=ActivityAnaSayfaBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val user = intent

        val gelenKisi=user.getSerializableExtra("user") as Users
        binding.textView3.text="Hoşgeldiniz "+gelenKisi.name

    }






}