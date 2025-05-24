package com.example.bfsapp.dao.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GraphEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)