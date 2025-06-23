package com.zj.itemtouchlistener

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

/**
 * CreateTime:2025/6/22 10:29
 * @author zhengjiong
 */
class AppIconAdapter(private val items: MutableList<String>) : RecyclerView.Adapter<AppIconAdapter.IconViewHolder>(){

    // 长按监听器
    var onItemLongClickListener: ((RecyclerView.ViewHolder) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        // 加载item布局
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.simple_list_item_1, parent, false)
        return IconViewHolder(view)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        // 绑定数据到ViewHolder
        holder.bind(items[position])

        // 设置长按监听
        holder.itemView.setOnLongClickListener {
            onItemLongClickListener?.invoke(holder)
            true
        }
    }

    override fun getItemCount(): Int = items.size

    // 自定义ViewHolder
    class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconText: TextView = itemView.findViewById(R.id.text2)

        // 绑定数据
        fun bind(item: String) {
            iconText.text = item
        }
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        // 使用更安全的交换方式
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(items, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(items, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        // 添加这行确保数据更新
        //notifyDataSetChanged()
    }
}