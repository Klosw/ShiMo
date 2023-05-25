package attach

fun String.replaceBadChar(): String {
    return this.replace("[\\?\\\\/:|<>\\*]".toRegex(), "_").replace("\\s+".toRegex(), "_")
}

