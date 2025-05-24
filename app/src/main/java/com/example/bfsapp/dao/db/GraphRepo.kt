package com.example.bfsapp.dao.db

import com.example.bfsapp.App
import com.example.bfsapp.dao.entities.EdgeEntity
import com.example.bfsapp.dao.entities.GraphEntity
import com.example.bfsapp.dao.entities.NodeEntity
import com.example.bfsapp.data.NodeInput

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GraphRepo {

    private val db = App.instance.db.graphDao()

    suspend fun saveGraph(name: String, nodes: List<NodeInput>) = withContext(Dispatchers.IO) {
        val graphId = db.insertGraph(GraphEntity(name = name)).toInt()

        val nodeEntities = nodes.mapIndexed { index, node ->
            NodeEntity(graphId = graphId, name = node.name, index = index)
        }
        db.insertNodes(nodeEntities)

        val edges = mutableListOf<EdgeEntity>()
        nodes.forEachIndexed { fromIndex, node ->
            node.connections.forEach { toIndex ->
                edges.add(EdgeEntity(graphId = graphId, fromIndex = fromIndex, toIndex = toIndex))
            }
        }
        db.insertEdges(edges)
    }

    suspend fun getAllGraphs(): List<GraphEntity> = withContext(Dispatchers.IO) {
        db.getAllGraphs()
    }

    suspend fun loadGraph(graphId: Int): List<NodeInput> = withContext(Dispatchers.IO) {
        val nodes = db.getNodes(graphId)
        val edges = db.getEdges(graphId)

        nodes.sortedBy { it.index }.map { node ->
            val connections = edges.filter { it.fromIndex == node.index }.map { it.toIndex }
            NodeInput(node.name, connections)
        }
    }

    suspend fun deleteGraph(graphId: Int) = withContext(Dispatchers.IO) {
        db.deleteEdgesByGraphId(graphId)
        db.deleteNodesByGraphId(graphId)
        db.deleteGraph(graphId)
    }
}

