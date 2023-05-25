object Args {
    fun parseArgs(args: Array<String>): HashMap<String, Array<String>> {
        val tmp = HashMap<String, Array<String>>()
        val arrValue = ArrayList<String>()
        var name: String? = null
        for (i in args.indices) {
            val arg = args[i]
            if (arg.startsWith("-")) {
                if (name != null) {
                    val argsValue = Array<String>(arrValue.size) { return@Array "" }
                    arrValue.toArray(argsValue)
                    tmp[name] = argsValue
                    arrValue.clear()
                }
                name = arg
            } else {
                arrValue.add(arg)
            }
        }
        if (name != null) {
            val argsValue = Array<String>(arrValue.size) { return@Array "" }
            arrValue.toArray(argsValue)
            tmp[name] = argsValue
            arrValue.clear()
        }
        return tmp
    }

}