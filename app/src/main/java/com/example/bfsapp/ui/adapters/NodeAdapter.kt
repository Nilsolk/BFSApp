package com.example.bfsapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.bfsapp.data.NodeInput
import com.example.bfsapp.databinding.NodeItemBinding

class NodeAdapter(private val listener: NodeListener) :
    RecyclerView.Adapter<NodeAdapter.NodeViewHolder>() {
    private val nodesList = mutableListOf<NodeInput>()

    class NodeViewHolder(val binding: NodeItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodeViewHolder {
        val binding = NodeItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NodeViewHolder(binding)
    }

    fun submitList(list: List<NodeInput>) {
        nodesList.clear()
        nodesList.addAll(list)
    }

    override fun getItemCount(): Int = nodesList.size

    override fun onBindViewHolder(holder: NodeViewHolder, position: Int) {
        val item = nodesList[position]
        with(holder.binding) {
            nodeIndex.text = "Вершина $position"

            nodeNameInput.setText(item.name.ifEmpty { "" })
            nodeConnectionsInput.setText(item.connections.joinToString(","))

            nodeNameInput.doOnTextChanged { text, _, _, _ ->
                listener.onNodeNameChanged(position, text.toString())
            }
            nodeConnectionsInput.doOnTextChanged { text, _, _, _ ->
                val ids = text?.split(",")?.mapNotNull { it.trim().toIntOrNull() } ?: listOf()
                listener.onNodeConnectionsChanged(position, ids)
            }
        }
    }
}