package com.zj.itemtouchlistener

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.View.DragShadowBuilder
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback


/**
 * CreateTime:2025/7/2 21:19
 * @author zhengjiong
 */
class DragItemTestActivity : ComponentActivity() {
    lateinit var view: View
    lateinit var targetView: View
    val tag = "DragItemTestActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_drag)

        view = findViewById(R.id.view)
        targetView = findViewById(R.id.target)


        view.setOnLongClickListener {
            val clipDataItem = ClipData.Item("这是拖动数据")
            val mimeTypes = arrayOf<String>(ClipDescription.MIMETYPE_TEXT_PLAIN) // MIME类型
            val dragData = ClipData("拖动标签", mimeTypes, clipDataItem) // 创建ClipData


            // 2. 创建本地状态对象（可选）
            val localState: Any = object : Any() {
                var extraInfo: String = "附加信息"
            }


            // 3. 创建拖动阴影（使用默认实现）
            val shadowBuilder1 = DragShadowBuilder(view)

            val shadowBuilder2: DragShadowBuilder = object : DragShadowBuilder(view) {
                override fun onProvideShadowMetrics(outShadowSize: Point?, outShadowTouchPoint: Point?) {
                    // 设置阴影大小和触摸点位置
                    super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint)
                    Log.i(tag, "onProvideShadowMetrics  设置阴影大小和触摸点位置")
                }

                public override fun onDrawShadow(canvas: Canvas) {
                    // 自定义绘制阴影外观
                    super.onDrawShadow(canvas)
                    Log.i(tag, "onDrawShadow 自定义绘制阴影外观")
                }
            }


            // 4. 设置拖动标志
            val flags = View.DRAG_FLAG_GLOBAL

            view.startDragAndDrop(dragData, shadowBuilder2, null, flags)
            //view.visibility = View.GONE
            //onBackPressedDispatcher.onBackPressed()
            true
        }




        targetView.setOnDragListener(object : View.OnDragListener {
            override fun onDrag(v: View, event: DragEvent?): Boolean {
                return when (event?.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        Log.i(tag, "drag_started")
                        // 拖动开始时调用
                        if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                            //v.setBackgroundColor(Color.LTGRAY)  // 改变背景色表示可以接收
                            true  // 返回true表示接收拖动事件
                        } else {
                            //false  // 不支持的MIME类型
                            true
                        }
                    }
                    DragEvent.ACTION_DRAG_LOCATION -> {
                        Log.i(tag, "drag_location 滑动中 x=${event.x}  y=${event.y}")
                        true
                    }
                    DragEvent.ACTION_DRAG_ENTERED -> {
                        Log.i(tag, "drag_entered 拖动进入目标View边界")
                        // 拖动进入目标View边界
                        //v.setBackgroundColor(Color.GRAY)
                        true
                    }

                    DragEvent.ACTION_DRAG_EXITED -> {
                        // 拖动离开目标View边界
                        Log.i(tag, "drag_exited 拖动离开目标View边")
                        //v.setBackgroundColor(Color.LTGRAY)
                        true
                    }

                    DragEvent.ACTION_DROP -> {
                        Log.i(tag, "action_drop 在目标View上放下")
                        // 在目标View上放下
                        val item = event.clipData.getItemAt(0)  // 获取数据

                        // 获取本地状态（如果有）
                        val localState = event.localState
                        if (localState != null) {
                            // 处理本地状态...
                        }

                        true
                    }

                    DragEvent.ACTION_DRAG_ENDED -> {
                        Log.i(tag, "drag_ended 拖动结束（无论成功与否） x=${event.x}  y=${event.y}")
                        // 拖动结束（无论成功与否）
                        //v.setBackgroundColor(Color.TRANSPARENT)
                        true
                    }

                    else -> true
                }
            }

        })


        // 注册返回按钮回调
        /**
         * 这里构造方法传输的参数true(OnBackPressedCallback(true))
         * 代表让当前addCallback添加进去的这个OnBackPressedCallback
         * 来拦截并处理返回逻辑,回调方法里又使用isEnabled = false,来
         * 让该OnBackPressedCallback不拦截返回逻辑,并又同时调用了
         * onBackPressedDispatcher.onBackPressed(),所以后面的
         * 逻辑就交给了其他的回调或者activity(如果现在是在fragment的话),
         * 现在没有其他回调或者其他activity处理的话,就走默认的返回
         * 逻辑:ComponentActivity.super.onBackPressed();
         */
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                Log.i(tag, "handleOnBackPressed")
                // 这里编写原本在onBackPressed()中的逻辑
                // 例如：
                if (shouldInterceptBackPress()) {
                    // 拦截返回操作
                    performCustomAction();
                } else {
                    /**
                     * 回调启用状态（enabled）
                     * true：表示拦截返回键事件，只会执行 handleOnBackPressed() 里的逻辑。
                     * false：表示不拦截返回键事件，返回事件会传递给其他回调或者 Activity。
                     *
                     * 事件传递机制
                     * 后注册的回调会先接收到返回事件（遵循 “后进先出” 原则）。
                     * 只有处于启用状态（enabled=true）的回调才会处理返回事件。
                     */
                    // 禁用当前回调（enabled=false），让返回事件传递给下一个回调或Activity
                    isEnabled = false

                    // 手动触发返回操作，由于当前回调已禁用，会执行默认行为
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun shouldInterceptBackPress(): Boolean {
        // 返回true表示拦截返回键，返回false则使用默认行为
        return false
    }

    private fun performCustomAction() {
        // 实现自定义操作
        finish()
    }

}