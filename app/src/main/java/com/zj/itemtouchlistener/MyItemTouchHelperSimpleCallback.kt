package com.zj.itemtouchlistener

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * CreateTime:2025/6/21 11:43
 * @author zhengjiong
 */
class MyItemTouchHelperSimpleCallback(val adapter: MyAdapter) : ItemTouchHelper.SimpleCallback(
    // 拖拽方向（上下）
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,

    // 滑动方向（左右）
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
) {

    // 当item被拖拽移动时调用
    override fun onMove(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
    ): Boolean {
        // 获取拖拽item的位置
        val fromPosition = viewHolder.absoluteAdapterPosition
        // 获取目标位置
        val toPosition = target.absoluteAdapterPosition

        println("onMove fromPosition=$fromPosition   toPosition$toPosition")
        // 通知适配器数据位置变化
        adapter.notifyItemMoved(fromPosition, toPosition)
        return false
    }

    // 当item被滑动删除时调用
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // 获取滑动item的位置
        val position = viewHolder.adapterPosition
        // 从数据源中移除该项
        adapter.removeItem(position)
        // 通知适配器数据移除
        adapter.notifyItemRemoved(position)
    }

    // 可选：自定义滑动或拖拽时的UI效果
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        // 这里可以自定义滑动时的item外观
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        // 根据ViewHolder类型返回不同的滑动/拖拽标志
        return makeMovementFlags(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
            //不允许swipe
            0
        )
    }
}