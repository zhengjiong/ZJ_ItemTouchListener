package com.zj.itemtouchlistener

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

/**
 * CreateTime:2025/6/21 14:10
 * @author zhengjiong
 */
class MyItemTouchHelperCallback(val adapter: MyAdapter) :ItemTouchHelper.Callback() {

    // 禁用滑动删除
    override fun isItemViewSwipeEnabled() = false

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        // 允许上下左右拖动
        return makeMovementFlags(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
            //不允许swipe
            0
        )
    }

    /**
     * 处理项目拖拽移动时的逻辑
     * 更新数据源和通知适配器
     * 当用户拖拽item时，ItemTouchHelper会持续调用此方法
     * 方法返回true表示处理了移动事件
     */
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        // 获取拖拽item的原始位置
        val fromPosition = viewHolder.absoluteAdapterPosition
        // 获取拖拽item的目标位置
        val toPosition = target.absoluteAdapterPosition
        println("onMove fromPosition$fromPosition , toPosition=$toPosition")


        Collections.swap(adapter.itemList, fromPosition, toPosition)
        /**
         * 通知适配器数据位置变化
         * notifyItemMoved() 是专门为位置变更设计的通知方法：
         * 它会触发 RecyclerView 的预定义移动动画
         * RecyclerView 能精确知道哪个项目从哪个位置移动到了哪个位置
         * 系统会自动计算新旧位置差异并生成平滑的过渡动画
         *
         * 通知 RecyclerView 某个项目的位置发生了变化, 不会调用 onBindViewHolder
         */
        adapter.notifyItemMoved(fromPosition, toPosition)



        /**
         * 拖拽过程中 ViewHolder 需要保持"活跃"状态
         * notifyDataSetChanged() 会强制重置所有 ViewHolder，破坏拖拽连续性
         */
        //Collections.swap(adapter.itemList, fromPosition, toPosition)
        //adapter.notifyDataSetChanged()
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }


    /**
     * 当item的交互状态改变时调用
     * 可用于改变选中item的外观
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
    }

    /**
     * 控制是否启用长按拖拽功能
     * 返回true时，用户长按item即可开始拖拽
     * 返回false时，需要通过代码手动启动拖拽
     */
    override fun isLongPressDragEnabled(): Boolean {
        return super.isLongPressDragEnabled()
    }
}