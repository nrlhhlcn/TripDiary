package com.mutu.tripdiary.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class Trip(
    val TripTitle: String,
    val TripDate: String,
    val TripImageId: Int,
    val TripInfo: String,
    val TripCountry: String,
    val TripCity: String,
    val CategoryId: Int,
    @PrimaryKey(autoGenerate = true) val TripId: Int = 0 ) {

}
