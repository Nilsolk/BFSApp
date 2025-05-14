package com.example.bfsapp.ui

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bfsapp.R
import com.example.bfsapp.view_models.DrawableViewModel

class DrawableFragment : Fragment() {

    companion object {
        fun newInstance() = DrawableFragment()
    }

    private val viewModel: DrawableViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_drawable, container, false)
    }
}