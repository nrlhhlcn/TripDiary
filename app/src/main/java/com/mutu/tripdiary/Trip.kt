package com.mutu.tripdiary

import android.graphics.Bitmap
import java.io.Serializable

class Trip(
    val tripid: String,
    val tripName: String,
    val title: String,
    val ani: String,
    val imagePath:String,
    val date:String,
    val tripCategory: String
):Serializable