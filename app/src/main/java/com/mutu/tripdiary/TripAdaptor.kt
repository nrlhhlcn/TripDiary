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
    override fun onBindViewHolder(holder: TripHolder, position: Int) {
        holder.binding.cardTitle.text = tripList[position].tripName
        holder.binding.cardDescription.text = tripList[position].ani

        // Görseli yükle
        val imagePath = tripList[position].imagePath
        if (imagePath.isNotEmpty()) {
            println("100000000000000000")
            println(imagePath)
            Glide.with(holder.binding.cardImage.context)
                .load(File(imagePath))
                .into(holder.binding.cardImage)
            println("200000000000000000000000")
        } else {
            // Eğer bir varsayılan resim göstermek istiyorsanız
            holder.binding.cardImage.setImageResource(R.drawable.user_pp)
        }


        holder.itemView.setOnClickListener{
            /* val intent= Intent(holder.itemView.context,DetaySayfasi::class.java)


            holder.itemView.context.startActivity(intent)*/
        }
    }

}