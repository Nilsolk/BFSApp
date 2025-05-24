package com.example.bfsapp.view_models

import androidx.lifecycle.*
import com.example.bfsapp.dao.db.GraphRepo
import com.example.bfsapp.data.NodeInput
import com.example.bfsapp.dao.entities.GraphEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val _nodes = MutableLiveData<List<NodeInput>>(emptyList())
    val nodes: LiveData<List<NodeInput>> = _nodes

    private val repository = GraphRepo()

    fun setNodeCount(count: Int) {
        val current = _nodes.value ?: emptyList()
        val newNodes = List(count) { i ->
            if (i < current.size) current[i] else NodeInput(
                name = "Вершина ${i + 1}",
                connections = emptyList()
            )
        }
        _nodes.value = newNodes
    }

    fun updateNodeName(position: Int, newName: String) {
        _nodes.value = _nodes.value?.toMutableList()?.apply {
            this[position] = this[position].copy(name = newName)
        }
    }

    fun updateNodeConnections(position: Int, connections: List<Int>) {
        _nodes.value = _nodes.value?.toMutableList()?.apply {
            this[position] = this[position].copy(connections = connections)
        }
    }

    fun setNodes(nodes: List<NodeInput>) {
        _nodes.postValue(nodes)
    }

    fun saveGraph(name: String) {
        val currentNodes = _nodes.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveGraph(name, currentNodes)
        }
    }

    suspend fun getAllGraphs(): List<GraphEntity> {
        return repository.getAllGraphs()
    }

    fun loadGraph(graphId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val nodes = repository.loadGraph(graphId)
            setNodes(nodes)
        }
    }

    fun deleteGraph(graphId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteGraph(graphId)
        }
    }
}
