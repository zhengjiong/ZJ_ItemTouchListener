package com.zj.itemtouchlistener

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * CreateTime:2025/6/21 11:25
 * @author zhengjiong
 */
class MyAdapter(private val items: List<String>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    val itemList = items.toMutableList()

    // 创建ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.simple_list_item_1, parent, false)
        return MyViewHolder(view)
    }

    // 绑定数据到ViewHolder
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = itemList[position]
    }

    // 获取item数量
    override fun getItemCount() = itemList.size

    // 移除item的方法
    fun removeItem(position: Int) {
        itemList.removeAt(position)
    }

    // ViewHolder类
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text2)
    }
}