package com.zj.itemtouchlistener

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * 自定义ItemTouchHelper回调，实现图标拖动效果
 * 功能特点：
 * 1. 长按图标会有缩放动画
 * 2. 拖动时保留原位置的图标（半透明显示）
 * 3. 拖动的是图标副本
 * 4. 支持网格布局中的位置交换
 *
 * @param adapter 关联的适配器，用于通知数据变化
 * @param recyclerView 关联的RecyclerView，用于获取位置信息
 */
class IconDragCallback(
    private val adapter: AppIconAdapter,
    private val recyclerView: RecyclerView
) : ItemTouchHelper.Callback() {

    // 保存原始图标的位置信息（相对于RecyclerView）
    private var originalIconRect: Rect? = null

    // 当前被拖动的ViewHolder
    private var draggedViewHolder: RecyclerView.ViewHolder? = null

    // 拖动状态标志，用于控制绘制逻辑
    private var isDragging = false

    // 动画持续时间（毫秒）
    private val animationDuration = 150L

    /**
     * 每次长按点击的时候会触发3次,但是都是一样的,会在onSelectedChanged之前触发.
     *
     * 设置支持的拖拽和滑动方向
     * @return 返回支持的标志位组合
     */
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        // 允许上下左右四个方向的拖动
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        // 禁用滑动删除功能
        println("getMovementFlags viewHolder position=${viewHolder.absoluteAdapterPosition}")
        return makeMovementFlags(dragFlags, 0)
    }

    /**
     * 当Item被拖动到新位置时调用
     * @return true表示处理了移动事件
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // 获取原始位置和目标位置
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition

        println("onMove fromPosition$fromPosition, toPosition=$toPosition")

        // 确保位置有效
        if (fromPosition == RecyclerView.NO_POSITION || toPosition == RecyclerView.NO_POSITION) {
            return false
        }

        // 通知适配器交换数据位置
        adapter.moveItem(fromPosition, toPosition)
        return true
    }

    /**
     * 当Item的拖动状态发生变化时调用
     * 当长按后会触发一次, 然后手离开屏幕的时候还会触发一次
     * 第一次是会返回按下的viewholder,actionState=ACTION_STATE_DRAG,
     * 第二次离开的时候viewHolder=null,actionState=ACTION_STATE_IDLE
     */
    override fun onSelectedChanged(
        viewHolder: RecyclerView.ViewHolder?,
        actionState: Int
    ) {
        super.onSelectedChanged(viewHolder, actionState)
        println("onSelectedChanged position=${viewHolder?.absoluteAdapterPosition}  actionState=$actionState")
        if (viewHolder == null) {
            return
        }
        when (actionState) {
            ItemTouchHelper.ACTION_STATE_DRAG -> {
                if (viewHolder != null) {
                    // 拖动开始
                    isDragging = true
                    draggedViewHolder = viewHolder

                    // 记录原始图标位置（考虑RecyclerView的滚动偏移）
                    originalIconRect = Rect().apply {
                        viewHolder.itemView.getGlobalVisibleRect(this)
                        // 转换为RecyclerView坐标系
                        offset(-recyclerView.scrollX, -recyclerView.scrollY)
                    }

                    // 执行缩放动画（不隐藏原Item）
                    //animateIcon(viewHolder.itemView)
                }
            }

            ItemTouchHelper.ACTION_STATE_IDLE -> {
                // 拖动结束，重置状态
                draggedViewHolder?.itemView?.alpha = 1f
                draggedViewHolder = null
                originalIconRect = null
                isDragging = false
            }
        }
    }

    /**
     * 也就是当交互完成时调用
     *
     * clearView并不是在手指离开后离开触发,而是在手指松开后,item回到该有的位置上后,
     * 当onChildDraw或者onChildDrawOver在快要变为0之前触发,
     * 简化后的长按滑动再松开的日志如下:
     *
     * getMovementFlags viewHolder position=0
     * onSelectedChanged position=0  actionState=2
     * onChildDraw position=0, actionState=2, isCurrentlyActive=true, dX=309.14212, dY=28.010529
     * onChildDrawOver position=0, actionState=drag, isCurrentlyActive=true, dX=309.14212, dY=28.010529
     * onMove fromPosition0, toPosition=1
     * onChildDraw position=1, actionState=2, isCurrentlyActive=true, dX=34.752563, dY=28.0
     * onChildDrawOver position=1, actionState=drag, isCurrentlyActive=true, dX=34.752563, dY=28.0
     * onSelectedChanged position=null  actionState=0 (手指离开屏幕)
     * onChildDraw position=1, actionState=2, isCurrentlyActive=false, dX=2.1756592, dY=-0.11079693
     * onChildDrawOver position=1, actionState=2, isCurrentlyActive=false, dX=2.1756592, dY=-0.11079693
     * clearView position=1  (手指离开屏幕,然后在onChildDraw dX=0.0, dY=0.0之前被调用, 也就是当交互完成时调用)
     * onChildDraw position=1, actionState=2, isCurrentlyActive=false, dX=0.0, dY=0.0
     * onChildDrawOver position=1, actionState=2, isCurrentlyActive=false, dX=0.0, dY=0.0
     *
     *
     */
    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        println("clearView position=${viewHolder.absoluteAdapterPosition}")
        super.clearView(recyclerView, viewHolder)
        // 确保恢复Item的原始状态
        viewHolder.itemView.alpha = 1f
        viewHolder.itemView.scaleX = 1f
        viewHolder.itemView.scaleY = 1f
        isDragging = false
    }

    /**
     * 执行图标的缩放动画
     */
    private fun animateIcon(view: View) {
        // 缩小动画（缩小到60%）
        val shrinkX = ObjectAnimator.ofFloat(view, "scaleX", 0.6f)
        val shrinkY = ObjectAnimator.ofFloat(view, "scaleY", 0.6f)

        // 放大动画（恢复到100%）
        val growX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f)
        val growY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f)

        AnimatorSet().apply {
            // 先缩小再放大
            playSequentially(
                AnimatorSet().apply { playTogether(shrinkX, shrinkY) },
                AnimatorSet().apply { playTogether(growX, growY) }
            )
            duration = animationDuration
            start()
        }
    }

    /**
     * 禁用长按拖动（由外部控制拖动开始）
     */
    override fun isLongPressDragEnabled(): Boolean = false

    /**
     * 禁用滑动删除
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    /**
     * 只要手指长按的时候就会一直触发,actionState=ACTION_STATE_DRAG,
     * 然后在onMove执行后,viewholder的position会发生改变,
     * 然后手指离开后,在onSelectedChanged执行后:onSelectedChanged position=null  actionState=0,
     * onChildDraw的isCurrentlyActive返回会改变为false,
     * onChildDrawOver和onChildDraw一样,只是在onChildDraw之后触发
     *
     * 拖动中的时候会回调, 可以在这里绘制覆盖在子项上方的自定义视觉效果
     *
     * @param c 用于绘制的 Canvas 对象
     * @param recyclerView 关联的 RecyclerView
     * @param viewHolder 当前正在被拖动/滑动的 ViewHolder
     * @param dX Item 在 X 轴上的位移（滑动时）
     * @param dY Item 在 Y 轴上的位移（滑动时）
     * @param actionState 当前的动作状态（拖动/滑动）
     * @param isCurrentlyActive 当前是否处于活动状态（用户正在操作）
     */
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        /**
         * 这里调用onChildDrawOver,目的是让onChildDraw按onChildDrawOver逻辑来触发,
         * 这样被拖动的view就可以被绘制在原位
         */
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        println("onChildDraw position=${viewHolder.absoluteAdapterPosition}, actionState=$actionState, isCurrentlyActive=$isCurrentlyActive, dX=$dX, dY=$dY")
    }

    /**
     * 拖动中的时候会回调, 可以在这里绘制覆盖在子项上方的自定义视觉效果
     * 自定义绘制逻辑（在Item绘制完成后调用）
     * @param c 用于绘制的 Canvas 对象
     * @param recyclerView 关联的 RecyclerView
     * @param viewHolder 当前正在被拖动/滑动的 ViewHolder
     * @param dX Item 在 X 轴上的位移（滑动时）
     * @param dY Item 在 Y 轴上的位移（滑动时）
     * @param actionState 当前的动作状态（拖动/滑动）
     * @param isCurrentlyActive 当前是否处于活动状态（用户正在操作）
     */
    override fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        // 仅对活跃的拖动项应用自定义绘制
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && isCurrentlyActive) {
            println("onChildDrawOver position=${viewHolder.absoluteAdapterPosition}, actionState=drag, isCurrentlyActive=$isCurrentlyActive, dX=$dX, dY=$dY")
            c.save()
            c.translate(
                viewHolder.itemView.left + dX,
                viewHolder.itemView.top + dY
            )
            viewHolder.itemView.alpha = 0.8f  // 可选：设置拖动时透明度
            viewHolder.itemView.draw(c)
            c.restore()
        } else {
            println("onChildDrawOver position=${viewHolder.absoluteAdapterPosition}, actionState=${actionState}, isCurrentlyActive=$isCurrentlyActive, dX=$dX, dY=$dY")
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }
}