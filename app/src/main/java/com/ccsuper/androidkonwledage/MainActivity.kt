package com.ccsuper.androidkonwledage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseArray
import android.widget.TextView
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        test()
        val r = Receiver();
        register
    }

    private fun test() {
        val list = listOf("1", "2", "3")
        val str = list[10]
        println(str)
    }

    class Receiver :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {

        }
    }
}