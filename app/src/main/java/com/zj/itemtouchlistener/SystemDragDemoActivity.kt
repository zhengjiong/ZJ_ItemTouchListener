package com.zj.itemtouchlistener

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.View.DragShadowBuilder
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import com.zj.itemtouchlistener.databinding.LayoutSystemDragDemoBinding

/**
 * CreateTime:2025/7/5 06:57
 * @author zhengjiong
 */
class SystemDragDemoActivity : ComponentActivity() {
    lateinit var binding: LayoutSystemDragDemoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LayoutSystemDragDemoBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val clipDataItem = ClipData.Item("这是拖动数据")
        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN) // MIME类型
        val dragData = ClipData("拖动标签", mimeTypes, clipDataItem) // 创建ClipData


        // 2. 创建本地状态对象（可选）
        val localState: Any = object : Any() {
            var extraInfo: String = "附加信息"
        }


        // 3. 创建拖动阴影（使用默认实现）
        val shadowBuilder1 = DragShadowBuilder(binding.dragView)

        binding.dragView.setOnLongClickListener {
            binding.dragView.startDragAndDrop(dragData, shadowBuilder1, binding.dragView, View.DRAG_FLAG_GLOBAL)
        }

        binding.leftTop.setOnDragListener(dragListener)
        binding.leftBottom.setOnDragListener(dragListener)
        binding.rightTop.setOnDragListener(dragListener)
        binding.rightBottom.setOnDragListener(dragListener)
    }

    val dragListener = View.OnDragListener { tagetView, event ->
        when (event.action) {
            /**
             * 拖动开始时调用
             * 必须返回 true 或者 false，表明自身是否为有效的拖放目标。只有返回 true 的视图，才会收到后续的拖放事件。
             */
            DragEvent.ACTION_DRAG_STARTED -> {
                println("tag->${tagetView.tag} started")
                true
            }

            // 拖动离开目标View边界
            DragEvent.ACTION_DRAG_EXITED -> {
                println("tag->${tagetView.tag} exited")
                true
            }

            // 拖动进入目标View边界
            DragEvent.ACTION_DRAG_ENTERED -> {
                println("tag->${tagetView.tag} entered")
                true
            }

            //拖拽阴影在 View 边界框内移动时发送
            DragEvent.ACTION_DRAG_LOCATION -> {
                //println("tag->${tagetView.tag} location")
                true
            }
            // 在目标View上放下
            DragEvent.ACTION_DROP -> {
                println("tag->${tagetView.tag} drop")

                //当前正在被拖拽的View
                val dragView = event.localState as View

                //tagetView是当前目标view,也就是dragView所拖动到的那个View
                val tagetViewId = tagetView.id

                val dragParentView = dragView.parent as ViewGroup
                when (dragParentView) {
                    binding.leftTop -> {
                        println("当前在左上角")
                    }
                    binding.rightTop -> {
                        println("当前在右上角")
                    }
                    binding.leftBottom -> {
                        println("当前在左下角")
                    }
                    binding.rightTop -> {
                        println("当前在右下角")
                    }
                }
                /**
                 * 这个判断表示当前没有滑动出左上角那个ViewGroup就不做处理
                 * tagetView是当前目标view,也就是dragView所拖动到的那个View,
                 * dragParentView是之前dragView的parent
                 */
                if (dragParentView.id == tagetViewId) {
                    println("没有拖动出之前的位置 直接返回")
                    return@OnDragListener false
                }

                dragParentView.removeView(dragView)
                //dragParentView.invalidate()

                tagetView as ViewGroup
                tagetView.addView(dragView)
                //tagetView.invalidate()

                when (tagetView.id) {
                    binding.leftTop.id ->{
                        binding.txt.text = "left top"
                    }
                    binding.rightTop.id ->{
                        binding.txt.text = "right top"
                    }
                    binding.leftBottom.id ->{
                        binding.txt.text = "left bottom"
                    }
                    binding.rightBottom.id ->{
                        binding.txt.text = "right bottom"
                    }
                }
                true
            }
            // 拖动结束（无论成功与否）
            DragEvent.ACTION_DRAG_ENDED -> {
                println("tag->${tagetView.tag} ended")
                true
            }

            else -> {
                println("tag->${tagetView.tag} else----")
                false
            }
        }
    }
}