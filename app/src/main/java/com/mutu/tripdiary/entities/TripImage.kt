package com.mutu.tripdiary.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tripimages")
data class TripImage (
    val TripImage1 : String,
    val TripImage2 : String?,
    val TripImage3: String?,
    val TripImage4: String?,
    val TripImage5: String?,
    @PrimaryKey(autoGenerate = true) val TripImageId: Int = 0 ) {


}

