import cn.hutool.core.date.DateUtil
import java.util.*
import kotlin.collections.ArrayList

object TokenPool {
    var isSwitch = true
    private var currentCookie: String = ""
    private val cookieAvailableTime = ArrayList<Long>()
    private val cookies = ArrayList<String>()
    fun getListCookies(): List<String> {
        return cookies.map { "shimo_sid=${it}" }
    }

    @Synchronized
    fun addCookies(cookie: String) {
        cookies.add(cookie)
        cookieAvailableTime.add(0)
    }

    @Synchronized
    fun removeCookies(cookie: String) {
        val indexOf = cookies.indexOf(cookie)
        cookies.removeAt(indexOf)
        cookieAvailableTime.removeAt(indexOf)
    }


    // 拿到一个 token
    fun getCookie(): String {
        currentCookie = getNextCookie()
        return "shimo_sid=${currentCookie}"
    }

    private fun getNextCookie(): String {
        var index = cookies.indexOf(currentCookie)
        if (index == -1) index = 0
        val longTime = cookieAvailableTime[index]
        return if (Date().time > longTime) {
            cookies[index]
        } else {
            if (isSwitch) {
                val min = cookieAvailableTime.min()
                val i = cookieAvailableTime.indexOf(min)
                val l = min - Date().time
                if (l > 0) {
                    Print.println("等待${l}ms")
                    Thread.sleep(l)
                }
                cookies[i]
            } else {
                val timex = longTime - Date().time
                Print.println("等待${timex}ms")
                Thread.sleep(timex)
                cookies[index]
            }
        }
    }

    //设置cookie 下次下载文件的可用时间
    //设置当前cookie的下次可用时间
    fun setCookieTimeOut() {
        val indexOf = cookies.indexOf(currentCookie)
        val beginOfMinute = DateUtil.beginOfMinute(DateUtil.offsetMinute(Date(), 1))
        cookieAvailableTime[indexOf] = beginOfMinute.time
    }
}
