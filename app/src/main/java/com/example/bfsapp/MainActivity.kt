package com.example.bfsapp

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.bfsapp.data.Graph
import com.example.bfsapp.databinding.ActivityMainBinding
import com.example.bfsapp.ui.custom_view.CustomSurfaceView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val graphView = binding.customSurfaceView

        val graph = Graph()
        graph.addNode(0, 300f, 300f)
        graph.addNode(1, 400f, 300f)
        graph.addNode(2, 300f, 600f)
        graph.addNode(3, 700f, 700f)

        graph.addEdge(0, 1)
        graph.addEdge(0, 2)
        graph.addEdge(1, 3)
        graph.addEdge(2, 3)

        graphView.setGraph(graph)
    }
}
