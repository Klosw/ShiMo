import bean.Data
import java.io.File
import java.io.FileOutputStream
import java.util.*

object Print {

    @Volatile
    var success = 0

    @Volatile
    var skip = 0

    @Volatile
    var fail = 0
    private val now = Date()

    private val logFile = File("shimo.log").apply {
        if (this.exists()) {
            this.delete()
        }
        kotlin.io.println("日志文件:${absolutePath}")
    }

    private val fileOut = FileOutputStream(logFile)

    init {
        addLogFile(now.toString())
    }

    fun addLogFile(msg: String) {
        fileOut.write(msg.toByteArray())
        fileOut.write("\r\n".toByteArray());
    }

    fun close() {
        println("任务完成 \n成功 $success \n失败 $fail \n跳过 $skip")
        Thread.sleep(1000)
        kotlin.io.println()
        println("完成用时:${(Date().time - now.time) / 1000}s")
        kotlin.io.println()
        addLogFile(Date().toString())
        fileOut.close()
    }

    fun println(msg: String) {
        print('\r')
        kotlin.io.print(msg)
        addLogFile(msg)
    }

}