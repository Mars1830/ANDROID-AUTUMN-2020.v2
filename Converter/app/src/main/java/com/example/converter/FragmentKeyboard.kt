package com.example.converter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.converter.databinding.FragmentKeyboardBinding


class FragmentKeyboard : Fragment() {

    private lateinit var activityCallback: MainActivity
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentKeyboardBinding


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallback = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(activityCallback).get(MainViewModel::class.java)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_keyboard,
            container,
            false
        )

        binding.btn0.setOnClickListener { keyboardClicked(0) }
        binding.btn1.setOnClickListener { keyboardClicked(1) }
        binding.btn2.setOnClickListener { keyboardClicked(2) }
        binding.btn3.setOnClickListener { keyboardClicked(3) }
        binding.btn4.setOnClickListener { keyboardClicked(4) }
        binding.btn5.setOnClickListener { keyboardClicked(5) }
        binding.btn6.setOnClickListener { keyboardClicked(6) }
        binding.btn7.setOnClickListener { keyboardClicked(7) }
        binding.btn8.setOnClickListener { keyboardClicked(8) }
        binding.btn9.setOnClickListener { keyboardClicked(9) }
        binding.del.setOnClickListener { keyboardClicked(10) }
        binding.dot.setOnClickListener { keyboardClicked(11) }
        return binding.root
    }

    private fun keyboardClicked(num: Int) {
        viewModel.keyboardClicked(num)
    }
}