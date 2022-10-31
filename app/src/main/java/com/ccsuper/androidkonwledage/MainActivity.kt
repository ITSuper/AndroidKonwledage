package com.ccsuper.androidkonwledage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        test()
    }

    private fun test() {
        val list = listOf("1", "2", "3")
        val str = list[10]
        println(str)
    }

    val list :CrashHandler by lazy {  CrashHandler() }
}