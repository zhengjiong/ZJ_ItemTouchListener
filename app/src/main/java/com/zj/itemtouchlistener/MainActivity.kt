package com.zj.itemtouchlistener

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main)

        initRecyclerView()
    }

    private fun initRecyclerView() {
        // 1. 初始化RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)

        // 2. 设置布局管理器
        recyclerView.layoutManager = GridLayoutManager(this, 4)

        val list = mutableListOf<String>()
        for (i in 0 until 30) {
            list.add("Item $i")
        }

        // 3. 创建并设置适配器
        val draggedPositions = mutableSetOf<Int>()
//        val adapter = MyAdapter(list)
        val adapter = AppIconAdapter(list)
        recyclerView.adapter = adapter

        // 4. 创建ItemTouchHelper回调
        //val itemTouchHelperCallback = MyItemTouchHelperSimpleCallback(adapter)
//        val itemTouchHelperCallback = MyItemTouchHelperCallback(adapter)
        val itemTouchHelperCallback = IconDragCallback(adapter, recyclerView)

        // 5. 创建ItemTouchHelper并附加到RecyclerView
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // 设置长按启动拖动
        adapter.onItemLongClickListener = { viewHolder ->
            itemTouchHelper.startDrag(viewHolder)
        }
    }
}