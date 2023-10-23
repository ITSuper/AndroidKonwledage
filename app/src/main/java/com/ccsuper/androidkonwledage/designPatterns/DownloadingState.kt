package com.ccsuper.androidkonwledage.designPatterns

import java.util.concurrent.FutureTask

class DownloadingState:DownloadState() {

    private var futureTask:FutureTask<Int> ?= null
    override fun download(url: String, filePath: String) {
        println("开始下载文件：$filePath")
//        val task  = DownloadTask(url,filePath)
//        futureTask = FutureTask(task)
    }

}