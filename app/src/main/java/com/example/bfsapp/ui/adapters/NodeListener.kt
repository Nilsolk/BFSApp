package com.example.bfsapp.ui.adapters

interface NodeListener {
    fun onNodeNameChanged(position: Int, newName: String)
    fun onNodeConnectionsChanged(position: Int, connections: List<Int>)
}