package com.ccsuper.androidkonwledage

import android.text.TextUtils
import android.util.Log
import java.io.*

/**
 * @Author Chen
 * @Date 2022/10/31-10:18
 * 类描述：
 */
class CrashHandler:Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        Log.e(
            "程序出现异常了", """
     Thread = ${t.name}
     Throwable = ${e.message}
     """.trimIndent()
        )
        val stackTraceInfo = getStackTraceInfo(e)
        Log.e("stackTraceInfo", stackTraceInfo)
        saveThrowableMessage(stackTraceInfo)
    }

    /**
     * 获取错误的信息
     *
     * @param throwable
     * @return
     */
    private fun getStackTraceInfo(throwable: Throwable): String {
        var pw: PrintWriter? = null
        val writer: Writer = StringWriter()
        try {
            pw = PrintWriter(writer)
            throwable.printStackTrace(pw)
        } catch (e: Exception) {
            return ""
        } finally {
            pw?.close()
        }
        return writer.toString()
    }

    private val logFilePath =
        ctx.externalCacheDir.toString() + File.separator + "Android" +
                File.separator + "data" + File.separator + App.instance
            .packageName + File.separator + "crashLog"

    private fun saveThrowableMessage(errorMessage: String) {
        if (TextUtils.isEmpty(errorMessage)) {
            return
        }
        FileSaveUtil.saveTextFile(App.instance.applicationContext,errorMessage)
//        val file = File(logFilePath)
//        if (!file.exists()) {
//            val mkdirs = file.mkdirs()
//            if (mkdirs) {
//                writeStringToFile(errorMessage, file)
//            }
//        } else {
//            writeStringToFile(errorMessage, file)
//        }
    }
    fun writeInputStreamToPath(
        ios: InputStream,
        dstDir: String,
        dstName: String,
        fileWriteListener: FileWriteListener? = null
    ): File? {
        return try {
            val f = File(dstDir,dstName)
            f.parentFile?.takeIf { !it.exists() }?.mkdirs()
            f.takeIf { it.exists() }?.delete()
            f.createNewFile()
            val fos = FileOutputStream(f)
            if (FileUtils.writeInputStreamToFileOutputStream(ios, fos, fileWriteListener)) {
                f
            } else null
        } catch (e: java.lang.Exception) {
            fileWriteListener?.onError(e)
            null
        }
    }
    private fun writeStringToFile(errorMessage: String, file: File) {
        Thread {
            var outputStream: FileOutputStream? = null
            try {
                val inputStream = ByteArrayInputStream(errorMessage.toByteArray())
                outputStream = FileOutputStream(
                    File(
                        file,
                        System.currentTimeMillis().toString() + ".txt"
                    )
                )
                var len = 0
                val bytes = ByteArray(1024)
                while (inputStream.read(bytes).also { len = it } != -1) {
                    outputStream.write(bytes, 0, len)
                }
                writeInputStreamToPath(inputStream,file.absolutePath,System.currentTimeMillis().toString() + ".txt")
                outputStream.flush()
                Log.e("程序出异常了", "写入本地文件成功：" + file.absolutePath)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }.start()
    }
}