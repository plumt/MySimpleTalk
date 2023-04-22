package com.yun.mysimpletalk.base

import android.graphics.Canvas
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.LayoutRes
import androidx.databinding.BaseObservable
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class BaseRecyclerAdapter {
    interface OnItemClickListener<T, V> {
        fun onItemClick(item: T, view: V)
        fun onItemLongClick(item: T, view: V): Boolean
    }

    abstract class Create<ITEM : Item, B : ViewDataBinding>(
        @LayoutRes private val layoutResId: Int,
        private val bindingVariableId: Int,
        private val bindingListener: Int
    ) : RecyclerView.Adapter<BaseViewHolder<B>>(), OnItemClickListener<ITEM, View>, Filterable {
        init {
            setHasStableIds(true)
        }


        var mItems = ListLiveData<ITEM>()

        fun attachSwipeToDelete(recyclerView: RecyclerView, callback: (ITEM) -> Unit) {
            val swipeToDeleteCallback = object : SwipeToDeleteCallback() {
                override fun onItemSwiped(position: Int) {
                    callback(mItems.get(position))
                    mItems.value!!.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
            val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
            itemTouchHelper.attachToRecyclerView(recyclerView)
        }

        override fun getFilter(): Filter? = null

        override fun getItemId(position: Int): Long {
            return mItems.get(position).id.toLong()
        }

        fun replaceAll(items: List<ITEM>, isClear: Boolean = true) {
            if (isClear) {
                mItems.clear(false)
            }
            mItems.addAll(items)
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return mItems.sizes()
                }

                override fun getNewListSize(): Int {
                    return items.size
                }

                override fun areItemsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return mItems.get(oldItemPosition).id == items[newItemPosition].id
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    val newProduct = items[newItemPosition]
                    val oldProduct = mItems.get(oldItemPosition)
                    return newProduct.id == oldProduct.id
                }
            })
            result.dispatchUpdatesTo(this)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<B> {
            val resId: Int = layoutResId

            val binding: B = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                resId,
                parent,
                false
            )
            binding.setVariable(bindingListener, this)
            return object : BaseViewHolder<B>(binding) {}
        }

        override fun onBindViewHolder(holder: BaseViewHolder<B>, position: Int) {
            holder.binding.setVariable(bindingVariableId, mItems.get(position))
            holder.binding.executePendingBindings()
        }

        override fun getItemCount() = mItems.sizes()
    }

    abstract class SwipeToDeleteCallback : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(0, swipeFlags)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            onItemSwiped(position)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val itemWidth = viewHolder.itemView.width.toFloat()
            val itemHeight = viewHolder.itemView.height.toFloat()
            val alpha = 1.0f - Math.abs(dX) / itemWidth
            viewHolder.itemView.alpha = alpha

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
            return 0.1f // 스와이프 거리 비율을 0.1로 설정합니다.
        }

        abstract fun onItemSwiped(position: Int)
    }

    abstract class BaseViewHolder<B : ViewDataBinding>(val binding: B) : RecyclerView.ViewHolder(
        binding.root
    )
}

abstract class Item : BaseObservable() {
    abstract var id: Int
}

@BindingAdapter("replaceAll")
fun RecyclerView.replace(list: List<Item>?) {
    list?.let {
        (adapter as? BaseRecyclerAdapter.Create<Item, *>)?.run {
            replaceAll(it)
        }
    }
}