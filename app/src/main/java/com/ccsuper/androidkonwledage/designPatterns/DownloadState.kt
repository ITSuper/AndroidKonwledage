package com.ccsuper.androidkonwledage.designPatterns

abstract class DownloadState {

    abstract fun download(url: String, filePath: String)
}