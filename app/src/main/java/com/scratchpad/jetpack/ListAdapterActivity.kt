package com.scratchpad.jetpack

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_list_adapter.*

class ListAdapterActivity : AppCompatActivity() {

    private val numbers = MutableLiveData<List<Int>>()

    private val diffCallback = object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int) =
            oldItem == newItem // check uniqueness

        override fun areContentsTheSame(oldItem: Int, newItem: Int) =
            oldItem == newItem // check contents
    }

    private val adapter: ListAdapter<Int, NumberViewHolder> = object : ListAdapter<Int, NumberViewHolder>(diffCallback) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            NumberViewHolder(parent)

        override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
            holder.bindTo(getItem(position))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_adapter)
        title = ListAdapterActivity::class.java.simpleName

        recyclerView.adapter = adapter.apply {
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    recyclerView.scrollToPosition(0)
                }
            })
        }

        numbers.apply {
            value = listOf(1, 4, 5, 6, 7, 8, 9, 10)
            observe(this@ListAdapterActivity, Observer<List<Int>> {
                adapter.submitList(it)
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_adapter, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.add -> numbers.value = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
            R.id.remove -> numbers.value = listOf(2, 4, 6, 8, 9)
            R.id.reorder -> numbers.value = numbers.value!!.shuffled()
        }
        return true
    }
}

private class NumberViewHolder(parentView: View) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context).inflate(R.layout.card_item, null, false)) {

    private val numberView = itemView.findViewById<TextView>(R.id.number)

    fun bindTo(position: Int) {
        numberView.text = "# $position"
    }
}
