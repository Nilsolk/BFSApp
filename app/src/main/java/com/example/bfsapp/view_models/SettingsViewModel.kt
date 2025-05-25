package com.example.bfsapp.view_models

import androidx.lifecycle.*
import com.example.bfsapp.dao.db.GraphRepo
import com.example.bfsapp.data.NodeInput
import com.example.bfsapp.dao.entities.GraphEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            withContext(Dispatchers.Main) {
                setNodes(nodes)
                setNodeCount(nodes.size)
            }
        }
    }

    fun deleteGraph(graphId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteGraph(graphId)
        }
    }

    fun insertPresetsIfNeeded() {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = repository.getAllGraphs()
            if (existing.isNotEmpty()) return@launch

            val presets = listOf(
                "Пресет (3 вершины)" to listOf(
                    NodeInput("A", listOf(1)),
                    NodeInput("B", listOf(0, 2)),
                    NodeInput("C", listOf(1))
                ),
                "Пресет (7 вершин)" to listOf(
                    NodeInput("A", listOf(1, 2)),
                    NodeInput("B", listOf(0, 3, 4)),
                    NodeInput("C", listOf(0, 5)),
                    NodeInput("D", listOf(1)),
                    NodeInput("E", listOf(1, 6)),
                    NodeInput("F", listOf(2)),
                    NodeInput("G", listOf(4))
                ),
                "Пресет (20 вершин)" to listOf(
                    NodeInput("V1", listOf(1, 5)),
                    NodeInput("V2", listOf(0, 2, 6)),
                    NodeInput("V3", listOf(1, 3)),
                    NodeInput("V4", listOf(2, 9)),
                    NodeInput("V5", listOf(0, 6, 10)),
                    NodeInput("V6", listOf(1, 5, 7, 11)),
                    NodeInput("V7", listOf(6, 8, 12)),
                    NodeInput("V8", listOf(7, 9, 13)),
                    NodeInput("V9", listOf(3, 8, 14)),
                    NodeInput("V10", listOf(4, 11, 15)),
                    NodeInput("V11", listOf(5, 10, 16)),
                    NodeInput("V12", listOf(6, 13, 17)),
                    NodeInput("V13", listOf(7, 12, 18)),
                    NodeInput("V14", listOf(8, 15, 19)),
                    NodeInput("V15", listOf(9, 14)),
                    NodeInput("V16", listOf(10, 17)),
                    NodeInput("V17", listOf(11, 16, 18)),
                    NodeInput("V18", listOf(12, 17, 19)),
                    NodeInput("V19", listOf(13, 18)),
                    NodeInput("V20", listOf(14))
                )
            )

            presets.forEach { (name, nodes) ->
                repository.saveGraph(name, nodes)
            }
        }
    }
}
