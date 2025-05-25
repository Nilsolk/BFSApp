package com.example.bfsapp.ui

import GraphListAdapter
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bfsapp.R
import com.example.bfsapp.databinding.DialogGraphListBinding
import com.example.bfsapp.databinding.FragmentSettingsBinding
import com.example.bfsapp.ui.adapters.NodeAdapter
import com.example.bfsapp.ui.adapters.NodeListener
import com.example.bfsapp.view_models.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var adapter: NodeAdapter
    private lateinit var dlAdapter: GraphListAdapter

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
                val arrayList = viewModel.nodes.value?.let { ArrayList(it) }
                putParcelableArrayList("nodes", arrayList)
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.containerFragment, DrawableFragment().apply { arguments = data })
                .addToBackStack(null)
                .commit()
        }

        binding.saveGraphButton.setOnClickListener {
            viewModel.saveGraph("Граф от ${System.currentTimeMillis()}")
        }

        binding.loadGraphButton.setOnClickListener {
            showGraphDialog()
        }
    }

    private fun showGraphDialog() {
        lifecycleScope.launch {
            val graphs = viewModel.getAllGraphs()
            if (graphs.isEmpty()) {
                Toast.makeText(requireContext(), "Нет сохранённых графов", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            }

            val dlgBinding = DialogGraphListBinding.inflate(layoutInflater, null, false)
            val dlg = AlertDialog.Builder(requireContext())
                .setView(dlgBinding.root)
                .create()

            dlAdapter = GraphListAdapter(
                graphs,
                onItemClick = { graph ->
                    viewModel.loadGraph(graph.id)
                    dlg.dismiss()
                },
                onDeleteClick = { graph ->
                    viewModel.deleteGraph(graph.id)
                    lifecycleScope.launch {
                        val updated = viewModel.getAllGraphs()
                        withContext(Dispatchers.Main) {
                            dlAdapter.update(updated)
                        }
                    }
                }
            )

            dlgBinding.graphList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = dlAdapter
            }

            dlgBinding.closeButton.setOnClickListener { dlg.dismiss() }
            dlg.show()
        }
    }


}
