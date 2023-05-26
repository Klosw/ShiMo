import java.io.File
import java.io.FileOutputStream

object Print {

    @Volatile
    var success = 0

    @Volatile
    var skip = 0

    @Volatile
    var fail = 0

    private val logFile = File("shimo.log").apply {
        if (this.exists()) {
            this.delete()
        }
    }
    private val fileOut = FileOutputStream(logFile)

    fun addLogFile(msg: String) {
        fileOut.write(msg.toByteArray())
        fileOut.write("\r\n".toByteArray());
    }

    fun close() {
        println("任务完成 \n成功 $success \n失败 $fail \n跳过 $skip")
        Thread.sleep(1000)
        fileOut.close()
    }

    fun println(msg: String) {
        kotlin.io.println(msg)
        addLogFile(msg)
    }

}