package com.example.converter

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.converter.databinding.FragmentMainBinding

class MainFragment : Fragment(),  AdapterView.OnItemSelectedListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var activityCallback: MainActivity
    private lateinit var binding: FragmentMainBinding

    /*override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val context: Context = requireContext()
        ArrayAdapter.createFromResource(
            context,
            R.array.categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.category.adapter = adapter
        }
    }*/

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallback = context as MainActivity
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main,
            container,
            false
        )

        viewModel = ViewModelProvider(activityCallback).get(MainViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        val context: Context = requireContext()

        ArrayAdapter.createFromResource(
            context,
            R.array.categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.category.adapter = adapter
        }

        binding.category.onItemSelectedListener = this
        binding.sourceUnit.onItemSelectedListener = this
        binding.destUnit.onItemSelectedListener = this

        viewModel.sourceStr.observe(viewLifecycleOwner, { calculateResult() })
    }

    private fun calculateResult() {
        setInputText()
        val result = viewModel.calculateResult().toString()
        setOutputText(result)
    }

    private fun categorySelected() {
        viewModel.category = binding.category.selectedItem.toString()
        updateUnits()
        calculateResult()
    }

    private fun sourceUnitSelected() {
        viewModel.sourceUnit = binding.sourceUnit.selectedItem.toString()
        calculateResult()
    }

    private fun destUnitSelected() {
        viewModel.destUnit = binding.destUnit.selectedItem.toString()
        calculateResult()
    }

    private fun updateUnits() {
        val units = when (viewModel.category) {
            "Distance" -> R.array.distanceUnits
            "Temperature" -> R.array.tempUnits
            "Weight" -> R.array.weightUnits
            else -> throw Exception("Resource not found")
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            units,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.sourceUnit.adapter = adapter
            binding.destUnit.adapter = adapter
        }
    }

    private fun setInputText() {
        binding.textInput.text = viewModel.sourceStr.value
    }

    private fun setOutputText(str: String) {
        binding.textOutput.text = str
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent) {
            binding.category -> categorySelected()
            binding.sourceUnit -> sourceUnitSelected()
            binding.destUnit -> destUnitSelected()
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}