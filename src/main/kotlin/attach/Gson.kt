package attach

import com.google.gson.Gson

val mGson = Gson()

fun <T> String.toBean(clazz: Class<T>): T {
    return mGson.fromJson(this, clazz)
}