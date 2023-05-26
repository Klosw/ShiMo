package attach

fun String.replaceBadChar(): String {
    return this.replace("[\\?\\\\/:|<>\\*]".toRegex(), "_").replace("\\s+".toRegex(), "_")
}

fun String.replaceFileBadChar(): String {
    return this.replace("[\\?\\\\/:|<>\\*]".toRegex(), "_")
}

