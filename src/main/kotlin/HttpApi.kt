import TokenPool.getListCookies
import attach.replaceFileBadChar
import attach.toBean
import bean.*
import cn.hutool.core.io.StreamProgress
import cn.hutool.http.HttpException
import cn.hutool.http.HttpRequest
import cn.hutool.http.HttpResponse
import cn.hutool.http.HttpUtil
import java.io.File
import kotlin.system.exitProcess


object HttpApi {
    private const val BASE_URL = "https://shimo.im/lizard-api/"
    private const val URL_FOLDER = "${BASE_URL}files?folder="

    private val headers = HashMap<String, String>().apply {
        put("Accept", "application/nd.shimo.v2+json")
        put("Accept-Encoding", "gzip, deflate, br")
        put("Accept-Language", "zh-CN,zh;q=0.9")
        put("Cache-Control", "no-cache")
        put(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36"
        )
        put("Referer", "https://shimo.im/panda-api/")
    }

    /*** 文件夹目录  */
    fun getFolder(folderId: String): Folder {
        return getHeadersRequestBody("${URL_FOLDER}${folderId}").toBean(Folder::class.java);
    }

    //获取团队控件所有空间名称
    fun getSpaces(): SpacesEntity {//@formatter:off
        val spaces: MutableList<Space> = ArrayList()
        val spacesEntity = SpacesEntity(spaces)
        val entity = getHeadersRequestBody("https://shimo.im/panda-api/file/spaces").toBean(SpacesEntity::class.java)
        val pinnedSpaces = getHeadersRequestBody("https://shimo.im/panda-api/file/pinned_spaces").toBean(SpacesEntity::class.java)

        entity.spaces?.apply {spacesEntity.spaces?.addAll(this)  }
        pinnedSpaces.spaces?.apply {spacesEntity.spaces?.addAll(this)  }
        return spacesEntity
    }//@formatter:on


    fun getMe(): MeEntity {
        return getHeadersRequestBody("https://shimo.im/lizard-api/users/me").toBean(MeEntity::class.java);
    }

    private fun export(uid: String, type: String): ExportEntity {
        val url = "https://shimo.im/lizard-api/office-gw/files/export?fileGuid=${uid}&type=${type}"
        val requestBody = getHeadersRequestBody(url)
//        println(requestBody)
        return requestBody.toBean(ExportEntity::class.java)
    }

    private fun getExportDownloadUrlAsync(taskId: String): ExDownLoadEntity {
        while (true) {
            val exportDownloadUrl = getExportDownloadUrl(taskId)
            if (exportDownloadUrl.data.progress == 100) {
                return exportDownloadUrl
            } else {
                Thread.sleep(1000)
                println("等待获取下载地址...")
            }
        }
    }

    private fun getExportDownloadUrl(taskId: String): ExDownLoadEntity {
        val url = "https://shimo.im/lizard-api/office-gw/files/export/progress?taskId=$taskId"
        val requestBody = getHeadersRequestBody(url)
//        println(requestBody)
        return requestBody.toBean(ExDownLoadEntity::class.java)
    }

    private fun getHeadersRequestBody(url: String, cookie: String = TokenPool.getCookie()): String {
        headers["Referer"] = url
        //获取一个 cookie
        headers["Cookie"] = cookie
        return HttpRequest.get(url).addHeaders(headers).execute().body()
    }

    /***
     * 缩略图
     */
    private fun thumbnail(item: FolderItem) {
        val url = "https://shimo.im/lizard-api/preview/${item.guid}/thumbnail"
        getHeadersRequestBody(url)
    }

    /**
     * 查询目录
     */
    fun ancestors(guid: String): AncestorsEntity {
        val url = "https://shimo.im/panda-api/file/files/${guid}/ancestors"
        return getHeadersRequestBody(url).toBean(AncestorsEntity::class.java)
    }

    fun downloadShiMo(item: FolderItem, folder: String) {
        println("${item.name}↓")
        val url = "https://shimo.im/lizard-api/files/${item.guid}/download"
        val file = File(folder, item.name.replaceFileBadChar())
        if (file.exists()) {
            println("${item.name} 跳过↑")
            return
        }
        downloadFile(url, file)
    }

    fun downloadExport(item: FolderItem, folder: String) {
        println("${item.name}↓↓")
        val fileType = TypeEnum.getFileType(item.type)
        val file = File(folder, "${item.name.replaceFileBadChar()}.${fileType}")
        if (file.exists()) {
            println("${item.name} 跳过↑↑")
            return
        }
        val export = export(item.guid, TypeEnum.getFileType(item.type))
        if (export.taskId != null) {
            val downloadUrl = getExportDownloadUrlAsync(export.taskId)
            if (downloadUrl.data.downloadUrl.isNullOrBlank()) {
                println("${item.name}○")
                TokenPool.setCookieTimeOut()
                //重新获取数据
                downloadExport(item, folder)
            } else {
                downloadFile(downloadUrl.data.downloadUrl, file)
                if (file.length() == downloadUrl.data.fileSize) {
                    println("文件下载 成功√")
                } else {
                    println("文件下载 失败×")
                    file.delete()
                }
            }
        } else {
            println("${item.name}●")
            TokenPool.setCookieTimeOut()
            downloadExport(item, folder)
        }
    }

    private fun downloadFile(
        url: String,
        targetFileOrDir: File,
        timeout: Int = -1,
        streamProgress: StreamProgress? = null,
    ): Long {
        return requestDownload(url, timeout).writeBody(targetFileOrDir, streamProgress)
    }

    private fun requestDownload(url: String, timeout: Int): HttpResponse {
        headers["Referer"] = url
        //获取一个 cookie
        headers["Cookie"] = TokenPool.getCookie()
        val response = HttpUtil.createGet(url, true).timeout(timeout).addHeaders(headers).executeAsync()
        if (response.isOk) {
            return response
        }
        throw HttpException("Server response error with status code: [{}]", response.status)
    }

    fun checkMeAll() {
        for (it in getListCookies()) {
            val meEntity =
                getHeadersRequestBody("https://shimo.im/lizard-api/users/me", it).toBean(MeEntity::class.java);
            if (meEntity.id == null) {
                println("cookie校验失败!")
                exitProcess(1)
            }
            println("团队: ${meEntity.team.name} 用户: ${meEntity.name} ")
        }
    }

}