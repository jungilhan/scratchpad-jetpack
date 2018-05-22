package com.scratchpad.jetpack

import android.arch.core.util.Function
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import kotlinx.android.synthetic.main.activity_recycler_view_selection.*

class RecyclerViewSelectionActivity : AppCompatActivity() {

    private val MAXIMUM_SELECTION = 5
    private lateinit var selectionTracker: SelectionTracker<Long>

        private val itemDetailsLookup = object : ItemDetailsLookup<Long>() {
        override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(e.x, e.y)
            if (view != null) {
                val holder = recyclerView.getChildViewHolder(view)
                (holder as? SelectableViewHolder).apply {
                    return object : ItemDetails<Long>() {
                        override fun getSelectionKey() = holder.itemId
                        override fun getPosition() = holder.adapterPosition
                    }
                }
            }
            return null
        }
    }

    private val selectionPredicate = object : SelectionTracker.SelectionPredicate<Long>() {
        override fun canSelectMultiple(): Boolean {
            return true
        }

        override fun canSetStateForKey(key: Long, nextState: Boolean): Boolean {
            return if (selectionTracker.selection.size() >= MAXIMUM_SELECTION && nextState) {
                Toast.makeText(this@RecyclerViewSelectionActivity,
                    "You can only select $MAXIMUM_SELECTION items in the list.", Toast.LENGTH_SHORT).show()
                false
            } else {
                true
            }
        }

        override fun canSetStateAtPosition(position: Int, nextState: Boolean): Boolean {
            return true
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view_selection)
        title = RecyclerViewSelectionActivity::class.java.simpleName

        SelectableAdapter(listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)).apply {
            selectionFun = Function {
                key -> selectionTracker.isSelected(key)
            }
            recyclerView.adapter = this
        }

        selectionTracker = SelectionTracker.Builder(
                "selection-demo",
                recyclerView,
                StableIdKeyProvider(recyclerView),
                itemDetailsLookup,
                StorageStrategy.createLongStorage())
            .withSelectionPredicate(selectionPredicate)
            .build()

        selectionTracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                title = if (selectionTracker.hasSelection()) {
                    "Selection ${selectionTracker.selection.size()} / $MAXIMUM_SELECTION"
                } else {
                    RecyclerViewSelectionActivity::class.java.simpleName
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        selectionTracker.onSaveInstanceState(outState!!)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        selectionTracker.onRestoreInstanceState(savedInstanceState)
    }
}

private class SelectableAdapter(private val items: List<Int>) : RecyclerView.Adapter<SelectableViewHolder>() {

    lateinit var selectionFun: Function<Long, Boolean>

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectableViewHolder
        = SelectableViewHolder(parent)

    override fun getItemCount()
        = items.size

    override fun getItemId(position: Int)
        = items[position].toLong()

    override fun onBindViewHolder(holder: SelectableViewHolder, position: Int) {
        holder.bindTo(items[position], selectionFun.apply(getItemId(position)))
    }
}

private class SelectableViewHolder(parentView: View) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context).inflate(R.layout.card_item, null, false)) {

    private val selectionView = itemView.findViewById<View>(R.id.selection)
    private val numberView = itemView.findViewById<TextView>(R.id.number)

    fun bindTo(position: Int, isSelected: Boolean) {
        selectionView.setBackgroundColor(
            if (isSelected) {
                itemView.isActivated = true
                Color.parseColor("#ff4081")
            } else {
                itemView.isActivated = false
                Color.parseColor("#dedede")
            })
        numberView.text = "# $position"
    }
}