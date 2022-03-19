package com.bauet.wesafe.ui.home.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.bauet.wesafe.databinding.FragmentCallHelplineNumbersBinding
import com.bauet.wesafe.ui.home.FacadeViewModel
import com.bauet.wesafe.utils.Validator
import com.bauet.wesafe.utils.hideKeyboard
import com.bauet.wesafe.utils.toast
import org.koin.android.ext.android.inject

class CallHelplineNumbersFragment : Fragment() {
    private val viewModel: FacadeViewModel by inject()

    private var binding: FragmentCallHelplineNumbersBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FragmentCallHelplineNumbersBinding.inflate(layoutInflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListener()
        initLoad()

    }

    private fun initLoad() {
        viewModel.getAllHelpLineNumbersInfo().observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                viewModel.insertMobileNumber("").observe(viewLifecycleOwner) {
                    viewModel.insertMobileNumber("").observe(viewLifecycleOwner) {
                        viewModel.insertMobileNumber("").observe(viewLifecycleOwner) {
                            initData()
                        }
                    }
                }
            } else {
                initData()
            }
        })
    }

    private fun initData() {
        viewModel.getNumberById(1).observe(viewLifecycleOwner, Observer {
            binding?.AmbulanceNumberET?.setText(it ?: "")
        })
        viewModel.getNumberById(2).observe(viewLifecycleOwner, Observer {
            binding?.womenHelplineNumberET?.setText(it ?: "")
        })
        viewModel.getNumberById(3).observe(viewLifecycleOwner, Observer {
            binding?.policeHelplineNumberET?.setText(it ?: "")
        })
    }

    private fun initClickListener() {
        binding?.saveAmbulanceButton?.setOnClickListener {
            if (validation(binding?.AmbulanceNumberET?.text.toString())) {
                viewModel.updateMobileNumber(binding?.AmbulanceNumberET?.text.toString(), 1)
                    .observe(viewLifecycleOwner, Observer {
                        if (it) {
                            context?.toast("Ambulance number saved Successfully")
                            hideKeyboard()
                            initData()
                        }
                    })
            } else {
                context?.toast("Please input valid ambulance number")
            }
        }
        binding?.callAmbulance?.setOnClickListener {
            viewModel.getNumberById(1).observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    checkPermission(it)
                }
            }
        }
        binding?.saveWomenHelplineButton?.setOnClickListener {
            if (validation(binding?.womenHelplineNumberET?.text.toString())) {
                viewModel.updateMobileNumber(binding?.womenHelplineNumberET?.text.toString(), 2)
                    .observe(viewLifecycleOwner, Observer {
                        if (it) {
                            context?.toast("Women helpline number saved Successfully")
                            hideKeyboard()
                            view?.clearFocus()
                            initData()
                        }
                    })
            } else {
                context?.toast("Please input valid Women Helpline number")
            }
        }
        binding?.callWomenHelpline?.setOnClickListener {
            viewModel.getNumberById(2).observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    checkPermission(it)
                }
            }
        }
        binding?.savepoliceHelplineButton?.setOnClickListener {
            if (validation(binding?.policeHelplineNumberET?.text.toString())) {
                viewModel.updateMobileNumber(binding?.policeHelplineNumberET?.text.toString(), 3)
                    .observe(viewLifecycleOwner, Observer {
                        if (it) {
                            context?.toast("Police number saved Successfully")
                            hideKeyboard()
                            initData()
                        }
                    })
            } else {
                context?.toast("Please input valid Police number")
            }
        }
        binding?.callPoliceHelpline?.setOnClickListener {
            viewModel.getNumberById(3).observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    checkPermission(it)
                }
            }
        }

    }

    private fun validation(number: String) = Validator.isValidMobileNumber(number)

    fun checkPermission(number: String) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CALL_PHONE)) {}
            else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), 42)
            }
        } else {
            callPhone(number)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == 42) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
               // callPhone(number)
            } else {}
            return
        }
    }

    private fun callPhone(number: String) {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
        startActivity(intent)
    }
}