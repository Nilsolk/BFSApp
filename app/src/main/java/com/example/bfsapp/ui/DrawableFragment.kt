package com.example.bfsapp.ui

import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bfsapp.R
import com.example.bfsapp.data.Graph
import com.example.bfsapp.data.NodeInput
import com.example.bfsapp.databinding.FragmentDrawableBinding
import com.example.bfsapp.view_models.DrawableViewModel

class DrawableFragment : Fragment() {

    private val viewModel: DrawableViewModel by viewModels()
    private lateinit var binding: FragmentDrawableBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nodes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList("nodes", NodeInput::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelableArrayList("nodes")
        }

        if (nodes != null) {
            viewModel.setNodes(nodes)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDrawableBinding.inflate(LayoutInflater.from(context))
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var startId = 0
        viewModel.nodes.observe(viewLifecycleOwner) { nodeList ->
            val graph = Graph()

            val bfsPositions = layoutByBfs(nodeList, startId = startId)

            nodeList.forEachIndexed { index, node ->
                val (x, y) = bfsPositions[index] ?: (0f to 0f)
                graph.addNode(index, x, y, node.name)
            }

            nodeList.forEachIndexed { index, node ->
                node.connections.forEach { to ->
                    graph.addEdge(index, to)
                }
            }

            binding.customSurfaceView.setGraph(graph)
            Log.d("DrawableFragment", "Graph set, nodes = ${nodeList.size}")
            binding.customSurfaceView.post {
                Log.d("DrawableFragment", "Posting startBfsAnimation")
                binding.customSurfaceView.startBfsAnimation(startNodeId = startId)
            }
        }
    }
}

private fun layoutByBfs(nodes: List<NodeInput>, startId: Int): Map<Int, Pair<Float, Float>> {
    val graph = Graph().apply {
        addNodesFromInput(nodes)
    }
    val levels = graph.getLevelGroups(startId)
    val positions = mutableMapOf<Int, Pair<Float, Float>>()
    levels.forEach { (level, ids) ->
        val y = 200f + level * 300f
        val step = 250f
        val totalWidth = step * (ids.size - 1)
        val startX = 600f - totalWidth / 2f
        ids.forEachIndexed { index, id ->
            val x = startX + index * step
            positions[id] = x to y
        }
    }

    return positions
}
