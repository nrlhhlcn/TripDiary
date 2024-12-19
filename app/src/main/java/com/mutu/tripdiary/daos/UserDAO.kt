package com.mutu.tripdiary.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mutu.tripdiary.entities.User

@Dao
interface UserDAO {

    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE UserId = :userId")
    suspend fun getUserById(userId : Int):User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("DELETE FROM users WHERE UserId = :userId")
    suspend fun deleteUserById(userId: Int)

    @Query("""
        UPDATE users
        SET name = :userName,
            surname = :userSurname,
            username = :userUsername,
            email = :userEmail,
            password = :userPassword
        WHERE UserId = :userId
    """)
    suspend fun updateUser(
        userName: String,
        userSurname: String,
        userUsername: String,
        userEmail: String,
        userPassword: String,
        userId : Int
    )
}