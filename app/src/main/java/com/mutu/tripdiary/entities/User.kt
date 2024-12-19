package com.mutu.tripdiary.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class User(
    val name:String,
    val surname:String,
    val username:String,
    val email:String,
    val password:String,
    @PrimaryKey(autoGenerate = true) val UserId: Int = 0) {

}