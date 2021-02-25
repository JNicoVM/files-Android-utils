@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject

fun haveError(jsonObject: JsonObject?): Boolean=
    jsonObject?.get("error")?.asInt!=0

fun getError(jsonObject: JsonObject): String = jsonObject.get("response").asString


fun getResponse(jsonObject: JsonObject): String = getError(jsonObject)

fun <T> fromJson(json: String, classOfT: Class<T>): T {
    return try {
        Gson().fromJson(json, classOfT)
    } catch (error: Exception) {
        Gson().fromJson("{}", classOfT)
    }
}

fun <T> fromJson(jsonObject: JsonObject, classOfT: Class<T>): T = fromJson(getResponse(jsonObject), classOfT)

fun <T> fromJson(jsonObject: JsonObject, clazz: Class<Array<T>>): List<T> = Gson().fromJson(getResponse(jsonObject), clazz).toList()

fun <T> fromJson(jsonObject: String, clazz: Class<Array<T>>): List<T> = Gson().fromJson(jsonObject, clazz).toList()

fun <T> getExtraCode(classOfT: Class<T>): String {
    classOfT.methods.forEach {
        if (it.name.contains("EXTRA")) {
            return it.invoke(null).toString()
        }
    }

    return classOfT.name
}

fun <T> Fragment.get(classOfT: Class<T>): T {
    val extraCode = getExtraCode(classOfT)
    val json = this.arguments?.getString(extraCode, "{}") ?: "{}"
    return fromJson(json, classOfT)
}

fun Fragment.put(any: Any) {
    val arguments = this.arguments ?: Bundle()
    arguments.putString(getExtraCode(any.javaClass), toJson(any))
    this.arguments = arguments
}

fun Intent.put(any: Any) {
    this.putExtra(any::class.java.name, toJson(any))
}
fun toJson(any: Any): String = Gson().toJson(any)

fun <T> Activity.get(classOfT: Class<T>): T {
    val json = this.intent.extras!!.getString(classOfT.name, "{}") ?: "{}"
    return fromJson(json, classOfT)
}

