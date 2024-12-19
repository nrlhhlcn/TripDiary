package com.mutu.tripdiary.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
class Category (
    val CategoryName : String,
    val CategoryImage: String,
    @PrimaryKey(autoGenerate = true) val CategoryId: Int = 0) {
}