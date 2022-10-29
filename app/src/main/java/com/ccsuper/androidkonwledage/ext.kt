package com.ccsuper.androidkonwledage

/**
 * @Author Chen
 * @Date 2022/10/29-14:20
 * 类描述：
 */
fun String.hello(work: String): String {
    replace("m", "")
    return "hello" + work + this.length;
}