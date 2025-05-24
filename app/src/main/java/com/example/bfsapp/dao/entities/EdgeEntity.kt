package com.example.bfsapp.dao.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class EdgeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val graphId: Int,
    val fromIndex: Int,
    val toIndex: Int
)