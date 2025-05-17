package com.example.bfsapp.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bfsapp.data.NodeInput

class DrawableViewModel : ViewModel() {
    private val _nodes = MutableLiveData<List<NodeInput>>()
    val nodes: LiveData<List<NodeInput>> = _nodes

    fun setNodes(list: List<NodeInput>) {
        _nodes.value = list
    }
}