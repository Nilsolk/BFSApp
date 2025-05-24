package com.example.bfsapp.dao.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bfsapp.dao.entities.EdgeEntity
import com.example.bfsapp.dao.entities.GraphEntity
import com.example.bfsapp.dao.entities.NodeEntity

@Dao
interface GraphDao {
    @Insert
    fun insertGraph(graph: GraphEntity): Long
    @Insert
    fun insertNodes(nodes: List<NodeEntity>)
    @Insert
    fun insertEdges(edges: List<EdgeEntity>)

    @Query("SELECT * FROM GraphEntity")
    fun getAllGraphs(): List<GraphEntity>
    @Query("SELECT * FROM NodeEntity WHERE graphId = :graphId")
    fun getNodes(graphId: Int): List<NodeEntity>
    @Query("SELECT * FROM EdgeEntity WHERE graphId = :graphId")
    fun getEdges(graphId: Int): List<EdgeEntity>

    @Query("DELETE FROM EdgeEntity WHERE graphId = :graphId")
    suspend fun deleteEdgesByGraphId(graphId: Int)

    @Query("DELETE FROM NodeEntity WHERE graphId = :graphId")
    suspend fun deleteNodesByGraphId(graphId: Int)

    @Query("DELETE FROM GraphEntity WHERE id = :graphId")
    suspend fun deleteGraph(graphId: Int)
}