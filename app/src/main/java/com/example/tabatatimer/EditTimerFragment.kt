package com.example.tabatatimer

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.tabatatimer.databinding.FragmentEditTimerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditTimerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditTimerFragment() : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewModel: EditTimerViewModel
    private lateinit var activityCallback: EditTimerActivity
    private lateinit var binding: FragmentEditTimerBinding
    private var thisSequence: Sequence? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallback = context as EditTimerActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_edit_timer,
            container,
            false
        )

        binding.okBtn.setOnClickListener { okBtnClicked() }
        binding.cancelBtn.setOnClickListener { cancelBtnClicked() }

        viewModel = ViewModelProvider(activityCallback).get(EditTimerViewModel::class.java)
        return binding.root

        //return inflater.inflate(R.layout.fragment_edit_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context: Context = requireContext()

        ArrayAdapter.createFromResource(
                context,
                R.array.sequence_colors_names,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerColor.adapter = adapter
        }

        binding.spinnerColor.setSelection(0)
    }

    private fun okBtnClicked() {
        try {
            val title = binding.editTitle.text.toString()
            val color = binding.spinnerColor.selectedItemPosition
            val prepTime = binding.editPrepTime.text.toString().toInt()
            val workTime = binding.editWorkTime.text.toString().toInt()
            val restTime = binding.editRestTime.text.toString().toInt()
            val cyclesNumber = binding.editCyclesNumber.text.toString().toInt()
            val cooldownTime = binding.editCooldownTime.text.toString().toInt()

            val newSequence = Sequence(title, color, prepTime, workTime, restTime, cyclesNumber, cooldownTime)
            //val newSequence = Sequence("title", 1, 1, 1, 1, 1, 1)
            GlobalScope.launch(Dispatchers.IO) {
                if (thisSequence == null) {
                    viewModel.createSequence(newSequence)
                }
                else {
                     viewModel.updateSequence(thisSequence!!, newSequence)
                }
            }
            activityCallback.onBackPressed()
        }
        catch (ex : NumberFormatException) {
            Toast.makeText(activityCallback, "Please enter correct data", Toast.LENGTH_LONG).show()
        }
    }

    fun setSequence(sequence: Sequence) {
        thisSequence = sequence
        binding.editTitle.setText(sequence.title)
        binding.spinnerColor.setSelection(sequence.color)
        binding.editPrepTime.setText(sequence.prepare.toString())
        binding.editWorkTime.setText(sequence.work.toString())
        binding.editRestTime.setText(sequence.rest.toString())
        binding.editCyclesNumber.setText(sequence.cycles.toString())
        binding.editCooldownTime.setText(sequence.cooldown.toString())
    }

    private fun cancelBtnClicked() {
        activityCallback.onBackPressed()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditTimerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditTimerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}