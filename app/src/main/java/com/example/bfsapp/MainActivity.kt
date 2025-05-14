package com.example.bfsapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bfsapp.data.Graph
import com.example.bfsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val graphView = binding.customSurfaceView

        val graph = Graph()

        graph.addNode(0, 200f, 200f)
        graph.addNode(1, 500f, 200f)
        graph.addNode(2, 800f, 200f)
        graph.addNode(3, 200f, 500f)
        graph.addNode(4, 500f, 500f)
        graph.addNode(5, 800f, 500f)
        graph.addNode(6, 200f, 800f)
        graph.addNode(7, 500f, 800f)
        graph.addNode(8, 800f, 800f)
        graph.addNode(9, 1100f, 500f)
        
        graph.addEdge(0, 1)
        graph.addEdge(1, 2)
        graph.addEdge(0, 3)
        graph.addEdge(1, 4)
        graph.addEdge(2, 5)
        graph.addEdge(3, 6)
        graph.addEdge(4, 7)
        graph.addEdge(5, 8)
        graph.addEdge(8, 9)
        graph.addEdge(7, 9)

        graphView.setGraph(graph)
        graphView.startBfsAnimation(startNodeId = 0)
    }
}
