package com.bauet.wesafe.ui.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.telephony.SmsManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bauet.wesafe.R
import com.bauet.wesafe.databinding.FragmentLoginBinding
import com.bauet.wesafe.ui.home.FacadeViewModel
import com.bauet.wesafe.ui.home.HomeActivity
import com.bauet.wesafe.utils.SessionManager
import com.bauet.wesafe.utils.toast
import com.google.android.gms.location.*
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null
    private val viewModel: FacadeViewModel by inject()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null
    var x = ""
    var y: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FragmentLoginBinding.inflate(layoutInflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intiClickListener()
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        getTheLocation()
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.SEND_SMS),
            1);
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            2);
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            3);
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.CALL_PHONE),
            4);
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.READ_PHONE_STATE),
            5);
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.ACCESS_NETWORK_STATE),
            6);
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.INTERNET),
            7);

    }

    fun getTheLocation() {
        locationRequest = LocationRequest().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                locationResult?.lastLocation?.let {
                    currentLocation = it
                    x = currentLocation?.latitude.toString()
                    y = currentLocation?.longitude.toString()
                }
            }
        }
    }

    private fun sendSms(number: String, msg: String) {
        SmsManager.getDefault().sendTextMessage(number, null, msg, null, null);
    }

    private fun intiClickListener() {
        binding?.registerButton?.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding?.loginButton?.setOnClickListener {
            if (validate()) {
                if (binding?.mobileNumberET?.text.toString() == SessionManager.mobile &&
                    binding?.passwordET?.text.toString() == SessionManager.password
                ) {
                    SessionManager.isLogin = true
                    startActivity(Intent(requireContext(), HomeActivity::class.java))
                } else if (SessionManager.mobile == "" && SessionManager.mobile == "") {
                    context?.toast("Please register first")
                } else {
                    context?.toast("You have input an incorrect Mobile number or password")
                }
            }
        }
        binding?.emergencyButton?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION),
                    1);
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION),
                    1);
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.myLooper())
            if (x.isNotEmpty() && y.isNotEmpty()) {
                val msg = "Help Me My Location is : http://maps.google.com/maps?saddr=$x,$y"
                viewModel.getAllContracts().observe(viewLifecycleOwner, Observer { list ->
                    if (list.isNotEmpty()) {
                        list.forEach { num -> sendSms(num, msg) }
                    }else{
                        context?.toast("You have no emergency contracts")
                    }
                })
                context?.toast("Message Sent Successfully")
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            } else {
                getTheLocation()
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