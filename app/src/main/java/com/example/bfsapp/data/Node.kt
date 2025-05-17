package com.example.bfsapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Node(
    val id: Int,
    var name: String = "",
    var posX: Float = 0f,
    var posY: Float = 0f,
    val neighbors: MutableList<Int> = mutableListOf()
)

@Parcelize
data class NodeInput(
    var name: String = "",
    var connections: List<Int> = emptyList()
) : Parcelable