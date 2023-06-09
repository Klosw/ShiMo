import TokenPool.getListCookies
import attach.encodeURL
import attach.replaceFileBadChar
import attach.toBean
import attach.toJson
import bean.*
import cn.hutool.core.io.StreamProgress
import cn.hutool.http.*
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
                Print.println("等待获取下载地址[${exportDownloadUrl.data.progress}]...")
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
        val url = "https://shimo.im/lizard-api/files/${item.guid}/download"
        val file = File(folder, item.name.replaceFileBadChar())
        if (file.exists()) {
            Print.skip++
            Print.println("跳过↑")
            return
        }
        downloadFile(url, file)
        Print.success++
    }

    fun downloadExport2(item: FolderItem, folder: String, retry: Int = 3) {
        val fileType = TypeEnum.getFileType(item.type)
        val file = File(folder, "${item.name.replaceFileBadChar()}.${fileType}")
        if (file.exists()) {
            Print.skip++
            Print.println("跳过↑↑↑")
            return
        }
        val url =
            "https://shimo.im/lizard-api/files/${item.guid}/export?type=${fileType}&file=${item.guid}&returnJson=1&name=${item.name.encodeURL()}&isAsync=0&timezoneOffset=-8"
        val entity = getHeadersRequestBody(url).toBean(RedirectEntity::class.java)
        if (retry == -1) {
            Print.fail++
            Print.println("下载失败 ${item.name}×")
            return
        }
        if (entity.redirectUrl == null) {
            Print.println("${item.name}●")
            TokenPool.setCookieTimeOut()
            downloadExport2(item, folder, retry - 1)
        } else {
            downloadFile(entity.redirectUrl, file)
            Print.success++
        }
    }

    fun downloadExport(item: FolderItem, folder: String) {
        val fileType = TypeEnum.getFileType(item.type)
        val file = File(folder, "${item.name.replaceFileBadChar()}.${fileType}")
        if (file.exists()) {
            Print.skip++
            Print.println("跳过↑↑")
            return
        }
        val export = export(item.guid, TypeEnum.getFileType(item.type))
        if (!export.taskId.isNullOrBlank()) {
            val downloadUrl = getExportDownloadUrlAsync(export.taskId)
            if (downloadUrl.data.downloadUrl.isNullOrBlank()) {
                Print.println("${item.name}○")
                TokenPool.setCookieTimeOut()
                //重新获取数据
                downloadExport(item, folder)
            } else {
                downloadFile(downloadUrl.data.downloadUrl, file)
                if (file.length() == downloadUrl.data.fileSize) {
                    Print.println("文件下载 成功√")
                    Print.success++
                } else {
                    Print.println("文件下载 失败×")
                    file.delete()
                    Print.fail++
                }
            }
        } else {
            //导出失败，请重试
            if (export.status == 120007) {
                Print.fail++
                Print.println("${export.message} 失败×")
                return
            }
            Print.println("${item.name}●")
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
                Print.println("cookie校验失败!")
                exitProcess(1)
            }
            val logs = "团队: ${meEntity.team?.name ?: "无"} 用户: ${meEntity.name} "
            Print.addLogFile(logs)
            println(logs)
        }
    }
}