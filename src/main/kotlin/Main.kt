import HttpApi.checkMeAll
import TokenPool.isSwitch
import bean.Ancestor
import java.io.File

private var ROOT_PATH = "团队空间/"
private var DESKTOP_PATH = "我的桌面/"
private var DESKTOP = "desktop"
private var SLEEP = 1000L

private const val COOKIES = "-c"
private const val PATH = "-path"
private const val FOLDER = "-f"
private const val HELP = "-h"
private const val HELP2 = "-help"


fun main(args: Array<String>) {
    val parseArgs = Args.parseArgs(args)
    if (parseArgs.contains(HELP) || parseArgs.contains(HELP2)) {
        val help = """
            version 1.1
            -path       文件下载路径
            -f          指定下载哪个文件夹数据 desktop 下载我的桌面数据 只下载第一个cookies的桌面
            -c          指定cookies可以传多个参数 例如 -c cookies1 cookies2 cookies3 cookies4
            -h -help    帮助文档
        """.trimIndent()
        println(help)
        return
    }

    if (parseArgs.contains(COOKIES)) {
        val cookiesList = parseArgs[COOKIES]
        if (cookiesList.isNullOrEmpty()) {
            println("未输入Cookies参考说明文档")
            return
        }
        cookiesList.forEach {
            TokenPool.addCookies(it)
        }
    } else {
        println("未输入Cookies参考说明文档")
        return
    }
    checkMeAll()
    val folderId = parseArgs[FOLDER]?.first()
    listSpaces(folderId ?: "")
}

fun listSpaces(folderId: String = "") {
    if (folderId.isBlank()) {
        val spaces = HttpApi.getSpaces()
        spaces.spaces?.forEach {
            list(it.guid, "${ROOT_PATH}${it.name}/")
        }
    } else {
        if (folderId.lowercase() == DESKTOP) {
            isSwitch = false
            list("", DESKTOP_PATH)
        } else {
            val ancestors = HttpApi.ancestors(folderId)
            val folderPath = ancestors.data.ancestors.map(Ancestor::name).joinToString("/")
            list(folderId, "${ROOT_PATH}${folderPath}/")
        }
    }
}

fun list(id: String, superior: String = "") {
    val folder = HttpApi.getFolder(id)
    folder.forEach { item ->
        if (item.isFolder) {
            list(item.guid, "${superior.trim()}${item.name.trim()}/")
        } else {
            val file = File(superior)
            if (!file.exists()) {
                file.mkdirs()
            }
            if (TypeEnum.isSkip(item.type)) {
                println("跳过 -> $superior${item.name}")
            } else {
                if (TypeEnum.isDownload(item.type) || !item.downloadUrl.isNullOrBlank()) {
                    HttpApi.downloadShiMo(item, file.absolutePath)
                    println("$superior${item.name}")
                } else {
                    when {
                        TypeEnum.isTransformation(item.type) -> {
                            //这里不想转换了
                            println("失败 -> $superior${item.name}")
                        }

                        TypeEnum.isOrderDownLoad(item.type) -> {
                            HttpApi.downloadExport2(item, file.absolutePath)
                            println("$superior${item.name}")
                        }

                        else -> {
                            HttpApi.downloadExport(item, file.absolutePath)
                            println("$superior${item.name}")
                        }
                    }
                }
            }
        }
    }
}