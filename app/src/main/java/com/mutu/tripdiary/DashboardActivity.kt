package com.mutu.tripdiary

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // SharedPreferences'den kullanıcı ID'sini al
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", -1)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Varsayılan olarak ilk fragmenti yükle ve userId'yi gönder
        replaceFragment(HomeFragment.newInstance(userId))

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment.newInstance(userId))
                R.id.nav_profile -> replaceFragment(ProfileFragment.newInstance(userId))
                R.id.nav_settings -> replaceFragment(SettingsFragment.newInstance(userId))
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}

