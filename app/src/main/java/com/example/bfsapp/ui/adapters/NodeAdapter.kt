package com.example.bfsapp.ui.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bfsapp.data.NodeInput
import com.example.bfsapp.databinding.NodeItemBinding

class NodeAdapter(private val listener: NodeListener) :
    ListAdapter<NodeInput, NodeAdapter.NodeViewHolder>(NodeDiffCallback()) {

    class NodeViewHolder(val binding: NodeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        var nodeNameWatcher: TextWatcher? = null
        var nodeConnectionsWatcher: TextWatcher? = null
        var internalChange = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodeViewHolder {
        val binding = NodeItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NodeViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            nodeIndex.text = "Вершина $position"

            // Удаляем старые слушатели, чтобы избежать утечек
            holder.nodeNameWatcher?.let { nodeNameInput.removeTextChangedListener(it) }
            holder.nodeConnectionsWatcher?.let { nodeConnectionsInput.removeTextChangedListener(it) }

            holder.internalChange = true
            if (nodeNameInput.text.toString() != item.name) {
                nodeNameInput.setText(item.name.ifEmpty { "" })
            }
            val connectionsText = item.connections.joinToString(",")
            if (nodeConnectionsInput.text.toString() != connectionsText) {
                nodeConnectionsInput.setText(connectionsText)
            }

            holder.internalChange = false


            holder.nodeNameWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (holder.internalChange) return
                    val pos = holder.adapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onNodeNameChanged(pos, s.toString())
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            }

            holder.nodeConnectionsWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (holder.internalChange) return
                    val pos = holder.adapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        val ids = s?.split(",")?.mapNotNull { it.trim().toIntOrNull() } ?: listOf()
                        listener.onNodeConnectionsChanged(pos, ids)
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            }

            nodeNameInput.addTextChangedListener(holder.nodeNameWatcher)
            nodeConnectionsInput.addTextChangedListener(holder.nodeConnectionsWatcher)
        }
    }
}

class NodeDiffCallback : DiffUtil.ItemCallback<NodeInput>() {
    override fun areItemsTheSame(oldItem: NodeInput, newItem: NodeInput): Boolean {
        return oldItem === newItem || oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: NodeInput, newItem: NodeInput): Boolean {
        return oldItem == newItem
    }
}
