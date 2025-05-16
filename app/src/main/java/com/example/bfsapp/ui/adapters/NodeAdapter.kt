package com.example.bfsapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.bfsapp.R
import com.example.bfsapp.data.NodeInput
import com.example.bfsapp.databinding.NodeItemBinding

class NodeAdapter : RecyclerView.Adapter<NodeAdapter.NodeViewHolder>() {
    private val nodesList = emptyList<NodeInput>()

    class NodeViewHolder(val binding: NodeItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodeViewHolder {
        val binding = NodeItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return NodeViewHolder(binding)
    }

    override fun getItemCount(): Int = nodesList.size

    override fun onBindViewHolder(holder: NodeViewHolder, position: Int) {
        val item = nodesList[position]
        with(holder.binding) {
            nodeIndex.text = "Вершина $position"

            nodeNameInput.setText(item.name)
            nodeConnectionsInput.setText(item.connections.toString())

            nodeNameInput.addTextChangedListener {
                item.name = it.toString()
            }
            nodeConnectionsInput.addTextChangedListener {
                item.connections = it?.split("")?.map { item -> item.toInt() } ?: emptyList()
            }
        }
    }
}