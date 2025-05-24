package com.example.bfsapp

import android.app.Application
import androidx.room.Room
import com.example.bfsapp.dao.db.GraphDatabase


class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    lateinit var db: GraphDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        db = Room.databaseBuilder(
            applicationContext,
            GraphDatabase::class.java,
            "graph_database"
        ).build()
    }
}
