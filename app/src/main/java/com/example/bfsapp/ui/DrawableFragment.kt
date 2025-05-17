package com.example.bfsapp.ui

import androidx.fragment.app.viewModels
import android.os.Bundle
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

        val nodes = arguments?.getParcelableArrayList<NodeInput>("nodes")
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

        viewModel.nodes.observe(viewLifecycleOwner) { nodeList ->
            val graph = Graph()

            val bfsPositions = layoutByBfs(nodeList, startId = 0)

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
            binding.customSurfaceView.startBfsAnimation(startNodeId = 0)
        }
    }
}

private fun layoutByBfs(nodes: List<NodeInput>, startId: Int): Map<Int, Pair<Float, Float>> {
    val graph = Graph()
    nodes.forEachIndexed { index, _ ->
        graph.addNode(index, 0f, 0f)
    }
    nodes.forEachIndexed { index, node ->
        node.connections.forEach { to ->
            graph.addEdge(index, to)
        }
    }

    val positions = mutableMapOf<Int, Pair<Float, Float>>()
    val visited = mutableSetOf<Int>()
    val queue = ArrayDeque<Pair<Int, Int>>()
    val levels = mutableMapOf<Int, MutableList<Int>>()

    queue.add(startId to 0)
    visited.add(startId)

    while (queue.isNotEmpty()) {
        val (node, level) = queue.removeFirst()
        levels.getOrPut(level) { mutableListOf() }.add(node)

        for (neighbor in graph.nodes[node]?.neighbors ?: emptyList()) {
            if (neighbor !in visited) {
                visited.add(neighbor)
                queue.add(neighbor to level + 1)
            }
        }
    }

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
