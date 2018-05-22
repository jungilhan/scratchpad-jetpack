package com.scratchpad.jetpack

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listAdapterButton.setOnClickListener { _ ->
            startActivity(Intent(MainActivity@this, ListAdapterActivity::class.java))
        }
        recyclerViewSelectionButton.setOnClickListener { _ ->
            startActivity(Intent(MainActivity@this, RecyclerViewSelectionActivity::class.java))
        }
    }
}
