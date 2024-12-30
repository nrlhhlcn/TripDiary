package com.mutu.tripdiary

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mutu.tripdiary.databinding.RecyclerRowBinding
import java.io.File

class TripAdaptor(val tripList:ArrayList<Trip>): RecyclerView.Adapter<TripAdaptor.TripHolder>()  {

    class TripHolder(val binding:RecyclerRowBinding):RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripHolder {
        val binding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TripHolder(binding)
    }
    override fun getItemCount(): Int {
        return  tripList.size
    }
    private fun getShortenedDescription(description: String): String {
        return if (description.length > 100) {
            description.substring(0, 100) + "..."
        } else {
            description
        }
    }
    override fun onBindViewHolder(holder: TripHolder, position: Int) {
        holder.binding.cardTitle.text = tripList[position].tripName.uppercase()
        holder.binding.cardDate.text=tripList[position].date.uppercase()

        holder.binding.cardDescription.text = getShortenedDescription(tripList[position].ani.capitalize())


        // Görseli yükle
        val imagePath = tripList[position].imagePath
        println(imagePath)
        if (imagePath.isNotEmpty()) {
            val firstImagePath = imagePath.split(",")[0] // Sadece ilk resmi al
            Glide.with(holder.binding.cardImage.context)
                .load(File(firstImagePath))
                .into(holder.binding.cardImage)
        }
        else {
            // Eğer bir varsayılan resim göstermek istiyorsanız
            holder.binding.cardImage.setImageResource(R.drawable.user_pp)
        }


        holder.itemView.setOnClickListener{
             val intent= Intent(holder.itemView.context,Detaylar::class.java)
             intent.putExtra("user",tripList[position])
            println(tripList[position].imagePath)

            holder.itemView.context.startActivity(intent)
        }
    }

}