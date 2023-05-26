//[zip, img, newdoc, xmind, modoc, mosheet, docx, unknown, presentation, mp4, mp3, pdf, ppt, slide, mindmap, xls, table, board]
enum class TypeEnum(private val type: String) {
    XLSX("xlsx"),//
    XLS("xlsx"),//
    MOSHEET("xlsx"),//
    spreadsheet("xlsx"),//
    sheet("xlsx"),//


    PPT("pptx"), //
    PRESENTATION("pptx"),//pptx,


    DOC("docx"), //
    DOCUMENT("docx"), //
    DOCX("docx"), //
    NEWDOC("docx"), //
    MODOC("docx"),//

    MINDMAP("xmind"),//
    XMIND("xmind"),//

    PDF("unknown"), //
    ZIP("unknown"), //
    IMG("unknown"), //
    MP3("unknown"), //
    MP4("unknown"), //
    UNKNOWN("unknown"),//

    SLIDE("pptx"), //

    BOARD("board"),//不知道啥
    TABLE("table"), //
    FOLDER("folder"),//
    SHORTCUT("shortcut"),//链接

    ;

    companion object {
        //另一种下载方式
        fun isOrderDownLoad(type: String): Boolean {
            return type.lowercase() == SLIDE.name.lowercase()
        }

        fun isTransformation(type: String): Boolean {
            return type == TABLE.name.lowercase()
        }

        /**
         * 部分无法下载的文件
         * 跳过不能下载的文件
         */
        fun isSkip(type: String): Boolean {
            return when (type) {
                BOARD.name.lowercase(),
                SHORTCUT.name.lowercase(),
                -> true

                else -> false
            }
        }

        /***
         * 使用直接下载的
         */
        fun isDownload(type: String): Boolean {
            val fileType = getFileType(type)
            return fileType == UNKNOWN.type
        }

        /***
         * 获取下载后缀
         */
        fun getFileType(type: String): String {
            for (it in values()) {
                if (it.name.lowercase() == type.lowercase()) {
                    return it.type
                }
            }
            return UNKNOWN.type
        }
    }
}