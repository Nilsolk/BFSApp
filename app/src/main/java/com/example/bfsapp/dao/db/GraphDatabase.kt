package com.example.bfsapp.dao.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bfsapp.dao.entities.EdgeEntity
import com.example.bfsapp.dao.entities.GraphEntity
import com.example.bfsapp.dao.entities.NodeEntity

@Database(entities = [GraphEntity::class, NodeEntity::class, EdgeEntity::class], version = 1)
abstract class GraphDatabase : RoomDatabase() {
    abstract fun graphDao(): GraphDao

    companion object {
        private var INSTANCE: GraphDatabase? = null
        fun getInstance(context: Context): GraphDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    GraphDatabase::class.java,
                    "graph_db"
                ).build().also { INSTANCE = it }
            }
        }
    }

}