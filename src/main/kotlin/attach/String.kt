package attach

import java.net.URLEncoder
import java.nio.charset.Charset

fun String.replaceBadChar(): String {
    return this.replace("[\\?\\\\/:|<>\\*]".toRegex(), "_").replace("\\s+".toRegex(), "_")
}

fun String.replaceFileBadChar(): String {
    return this.replace("[\\?\\\\/:|<>\\*]".toRegex(), "_").trim()
}

fun String.encodeURL(): String {
    return URLEncoder.encode(this, Charset.defaultCharset())
}

