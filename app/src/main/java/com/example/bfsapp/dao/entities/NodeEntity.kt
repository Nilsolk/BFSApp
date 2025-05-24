package com.example.bfsapp.dao.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val graphId: Int,
    val name: String,
    val index: Int
)