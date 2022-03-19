package com.bauet.wesafe.ui.home

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bauet.wesafe.repository.AppRepository
import com.bauet.wesafe.utils.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FacadeViewModel(private val repository: AppRepository) : ViewModel() {
    val viewState = MutableLiveData<ViewState>(ViewState.NONE)
    val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"

    val currentLocation = MutableLiveData<Location?>(null)


    fun getAllContracts(): LiveData<List<String>> {
        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<List<String>>()
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getAllContracts()
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                responseBody.value = response
            }
        }
        return responseBody
    }

    fun insertData(mobileNo: String): LiveData<Boolean> {
        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<Boolean>(false)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.insertData(mobileNo)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                responseBody.value = true
            }
        }
        return responseBody
    }

    fun deleteMobileNumber(mobileNo: String): LiveData<Boolean> {
        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<Boolean>(false)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.deleteMobileNumber(mobileNo)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                responseBody.value = true
            }
        }
        return responseBody
    }

    fun getNumberById(id: Int): LiveData<String> {
        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<String>()
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getNumberById(id)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                responseBody.value = response
            }
        }
        return responseBody
    }
    fun getAllHelpLineNumbersInfo(): LiveData<List<String>> {
        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<List<String>>()
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getAllHelpLineNumbersInfo()
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                responseBody.value = response
            }
        }
        return responseBody
    }

    fun updateMobileNumber(mobileNo: String, id: Int): LiveData<Boolean> {
        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<Boolean>(false)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.updateMobileNumber(mobileNo, id)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                responseBody.value = true
            }
        }
        return responseBody
    }
    fun insertMobileNumber(mobileNo: String): LiveData<Boolean> {
        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<Boolean>(false)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.insertMobileNumber(mobileNo)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                responseBody.value = true
            }
        }
        return responseBody
    }
}