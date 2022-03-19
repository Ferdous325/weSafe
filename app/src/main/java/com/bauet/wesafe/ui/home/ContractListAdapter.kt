package com.bauet.wesafe.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bauet.wesafe.databinding.ItemViewIndividualContractBinding

class ContractListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dataList: MutableList<String> = mutableListOf()
    var onDeleteClicked: ((num: String)-> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(
            ItemViewIndividualContractBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val binding = holder.binding
            binding.contractId.text = dataList[position]
        }
    }

    inner class ViewHolder(val binding: ItemViewIndividualContractBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.deleteButton.setOnClickListener {
                onDeleteClicked?.invoke(dataList[absoluteAdapterPosition])
            }
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun initLoad(data: List<String>) {
        dataList.clear()
        dataList.addAll(data)
        notifyDataSetChanged()
    }
}