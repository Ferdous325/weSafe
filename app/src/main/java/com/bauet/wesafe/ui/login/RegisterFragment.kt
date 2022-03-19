package com.bauet.wesafe.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bauet.wesafe.R
import com.bauet.wesafe.databinding.FragmentRegisterBinding
import com.bauet.wesafe.ui.home.HomeActivity
import com.bauet.wesafe.utils.SessionManager
import com.bauet.wesafe.utils.toast


class RegisterFragment : Fragment() {

    private var binding: FragmentRegisterBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentRegisterBinding.inflate(layoutInflater).also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListener()
    }

    private fun initClickListener() {
        binding?.registerButton?.setOnClickListener {
            if (validate()) {
                SessionManager.isLogin = true
                SessionManager.password = binding?.passwordET?.text.toString()
                SessionManager.mobile = binding?.mobileNumberET?.text.toString()
                startActivity(Intent(requireContext(), HomeActivity::class.java))
            }
        }
    }

    private fun validate(): Boolean {
        if (binding?.mobileNumberET?.text.toString().isEmpty()) {
            context?.toast("Please input mobile number"); return false;
        }
        if (binding?.passwordET?.text.toString().isEmpty()) {
            context?.toast("Please input password"); return false;
        }
        return true
    }
}