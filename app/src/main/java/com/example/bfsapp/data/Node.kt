package com.example.bfsapp.data

data class Node(
    val id: Int,
    var posX: Float = 0f,
    var posY: Float = 0f,
    val neighbors: MutableList<Int> = mutableListOf()
)