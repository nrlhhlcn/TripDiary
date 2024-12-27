package com.mutu.tripdiary

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
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


        // Veritabanında trip tablosunu kontrol et
        val database = this.openOrCreateDatabase("TripDiary", Context.MODE_PRIVATE, null)
        val tripTableExists = checkIfTripTableExists(database)

        // Tablo durumuna göre başlangıç fragmentini belirle
        if (tripTableExists) {
            replaceFragment(HomeFragment.newInstance(userId))
        } else {
            replaceFragment(SettingsFragment.newInstance(userId))
        }
        // Varsayılan olarak ilk fragmenti yükle ve userId'yi gönder
        //replaceFragment(HomeFragment.newInstance(userId))

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

    private fun checkIfTripTableExists(database: android.database.sqlite.SQLiteDatabase): Boolean {
        val cursor = database.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name='trip';",
            null
        )
        val tableExists = cursor.count > 0
        cursor.close()
        return tableExists
    }
}



