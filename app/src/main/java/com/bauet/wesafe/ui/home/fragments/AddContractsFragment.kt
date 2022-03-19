package com.bauet.wesafe.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bauet.wesafe.databinding.FragmentAddContractsBinding
import com.bauet.wesafe.ui.home.ContractListAdapter
import com.bauet.wesafe.ui.home.FacadeViewModel
import com.bauet.wesafe.utils.Validator
import com.bauet.wesafe.utils.toast
import org.koin.android.ext.android.inject

class AddContractsFragment : Fragment() {

    private var binding: FragmentAddContractsBinding? = null
    private val dataAdapter = ContractListAdapter()
    private val viewModel: FacadeViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FragmentAddContractsBinding.inflate(layoutInflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intiClickListener()
        initView()
        initLoad()
    }

    private fun initLoad() {
        viewModel.getAllContracts().observe(viewLifecycleOwner, Observer {
            dataAdapter.initLoad(it)
        })
    }

    private fun initView() {
        with(binding?.contractListRecycler!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            animation = null
        }
    }

    private fun intiClickListener() {
        dataAdapter.onDeleteClicked = { number ->
            viewModel.deleteMobileNumber(number).observe(viewLifecycleOwner, Observer {
                if (it) {
                    initLoad()
                }
            })
        }
        binding?.addButton?.setOnClickListener {
            if (Validator.isValidMobileNumber(binding?.contractNumber?.text.toString())) {
                viewModel.insertData(binding?.contractNumber?.text.toString())
                    .observe(viewLifecycleOwner, Observer {
                        if (it) {
                            binding?.contractNumber?.setText("")
                            initLoad()
                        }
                    })
            } else {
                context?.toast("Please enter a valid mobile number")
            }
        }
    }
}
