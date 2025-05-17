package com.example.bfsapp.ui

import android.os.Bundle
import android.view.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bfsapp.R
import com.example.bfsapp.databinding.FragmentSettingsBinding
import com.example.bfsapp.ui.adapters.NodeAdapter
import com.example.bfsapp.ui.adapters.NodeListener
import com.example.bfsapp.view_models.SettingsViewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var adapter: NodeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = NodeAdapter(object : NodeListener {
            override fun onNodeNameChanged(position: Int, newName: String) {
                viewModel.updateNodeName(position, newName)
            }

            override fun onNodeConnectionsChanged(position: Int, connections: List<Int>) {
                viewModel.updateNodeConnections(position, connections)
            }
        })

        binding.nodeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.nodeRecyclerView.adapter = adapter

        binding.nodeCountInput.doOnTextChanged { text, _, _, _ ->
            val count = text?.toString()?.toIntOrNull() ?: return@doOnTextChanged
            viewModel.setNodeCount(count)
        }

        viewModel.nodes.observe(viewLifecycleOwner, Observer { nodeList ->
            adapter.submitList(nodeList.toList())
        })



        binding.buildGraphButton.setOnClickListener {
            val data = Bundle().apply {
                val arrayList = viewModel.nodes.value?.let { it1 -> ArrayList(it1) }
                putParcelableArrayList("nodes", arrayList)
            }

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.containerFragment,
                    DrawableFragment().apply { arguments = data },
                    DrawableFragment::class.java.name
                )
                .addToBackStack(null)
                .commit()
        }
    }
}
