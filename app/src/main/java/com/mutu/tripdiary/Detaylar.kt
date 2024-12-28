package com.mutu.tripdiary

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.mutu.tripdiary.databinding.ActivityDetaylarBinding
import com.mutu.tripdiary.databinding.ActivityMainBinding
import java.io.File


class Detaylar : AppCompatActivity() {
    private lateinit var binding: ActivityDetaylarBinding
    private lateinit var trip: Trip // Sınıf düzeyinde tanımlayın
    private var arttir = 0 // Başlangıç değeri sıfır

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetaylarBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // `trip` nesnesini intent'ten alıyoruz
        trip = intent.getSerializableExtra("user") as Trip
        binding.tripNameText.text = trip.tripName
        binding.tripTarihText.text = trip.date
        binding.tripKategoriText.text = trip.tripCategory
        binding.tripInfoText.text = trip.ani

        // İlk resmi yükle

        val imagePaths = trip.imagePath.split(",")
        println(imagePaths)// Tüm resim yollarını listeye dönüştür
        Glide.with(this)
            .load(File(imagePaths[0])) // İlk resmi göster
            .into(binding.imageView)
    }

    fun resimDegistir(view: View) {
        val imagePaths = trip.imagePath.split(",") // Virgülle ayrılmış yolları parçala

        if (imagePaths.isNotEmpty()) {
            // `arttir` değişkeniyle resim listesini dolaş
            arttir = (arttir + 1) % imagePaths.size // Mod alma işlemiyle baştan başlat
            loadImage(imagePaths[arttir]) // Sıradaki resmi yükle
        }
    }

    fun resimBuyult(view: View) {
        // AlertDialog.Builder ile yeni bir dialog oluşturuyoruz
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Görsel Gösterimi")


        // Yeni bir ImageView oluşturuyoruz
        val imageViewCopy = ImageView(this)
        imageViewCopy.setImageDrawable(binding.imageView.drawable) // Görseli kopyalıyoruz

        // Görselin boyutlarını ayarlamak için LayoutParams kullanıyoruz
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, // Genişlik: Ekranın tamamı
            LinearLayout.LayoutParams.WRAP_CONTENT // Yükseklik: İçeriğe göre uyarlanacak
        )
        layoutParams.setMargins(20, 20, 20, 20) // Görsele etrafına padding ekliyoruz
        imageViewCopy.layoutParams = layoutParams

        // Resmi kaydırılabilir hale getirebilmek için ScrollView ekliyoruz
        val scrollView = ScrollView(this)
        scrollView.addView(imageViewCopy)

        // ScrollView'un genişlik ve yüksekliğini kontrol etmek için bazı sınırlamalar ekliyoruz
        val scrollViewParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, // ScrollView'un genişliği ekranın tamamı
            LinearLayout.LayoutParams.MATCH_PARENT  // ScrollView'un yüksekliği ekranın tamamı
        )
        scrollView.layoutParams = scrollViewParams

        // Dialog içeriğini ayarlıyoruz
        builder.setView(scrollView)

        // Pozitif buton ekliyoruz
        builder.setPositiveButton("Küçült") { dialog, _ ->
            dialog.dismiss() // Dialog'u kapat
        }

        // Stil eklemek için şık bir görünüm kullanabiliriz
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.drawable.dialog_holo_light_frame) // Arka planı değiştir
        alertDialog.show()

        // Butonun şık görünmesi için
        val button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        button.setBackgroundColor(resources.getColor(R.color.teal_200))
        button.setTextColor(resources.getColor(R.color.white))
        button.setTextSize(18f)
        button.setPadding(20, 10, 20, 10) // Butona padding ekleyebiliriz
    }

    fun resimDegistir2(view: View) {
        val imagePaths = trip.imagePath.split(",") // Virgülle ayrılmış yolları parçala

        if (imagePaths.isNotEmpty()) {
            // `arttir` değişkenini azalt, negatif olmaması için kontrol et
            arttir = if (arttir - 1 < 0) imagePaths.size - 1 else arttir - 1
            loadImage(imagePaths[arttir]) // Sıradaki resmi yükle
        }
    }


    private fun loadImage(imagePath: String) {
        Glide.with(this)
            .load(File(imagePath))
            .into(binding.imageView)
    }
}
