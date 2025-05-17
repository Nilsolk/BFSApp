package com.example.bfsapp.view_models

import android.view.inputmethod.InputConnection
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bfsapp.data.NodeInput

class SettingsViewModel : ViewModel() {

    private val _nodes = MutableLiveData<List<NodeInput>>()
    val nodes: LiveData<List<NodeInput>> get() = _nodes

    fun setNodeCount(count: Int) {
        _nodes.value = List(count) { NodeInput() }
    }

    fun updateNodeName(index: Int, name: String) {
        val current = _nodes.value?.toMutableList() ?: return
        val node = current[index]
        node.name = name
        _nodes.value = current
    }

    fun updateNodeConnections(index: Int, connections: List<Int>) {
        val current = _nodes.value?.toMutableList() ?: return
        val node = current[index]
        node.connections = connections
        _nodes.value = current
    }
}