@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

import android.content.Context
import com.wigilabs.progressus.BuildConfig
import com.wigilabs.progressus.R
import com.wigilabs.progressus.utils.AESCrypt


fun <T> String.decrypt(classOfT: Class<T>): T {
    val json = AESCrypt.decrypt(classOfT.name, this)
    return fromJson(json, classOfT)
}

fun Any.encrypt(): String = AESCrypt.encrypt(this::class.java.name, toJson(this))

fun Context.save(data: Any) {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences), Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(getExtraCode(data.javaClass), data.encrypt())
    editor.apply()
}

fun Context.save(llave: String, valor: String) {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences), Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(llave, valor)
    editor.apply()
}

fun Context.save(llave: String, valor: Boolean) {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences),
            Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean(llave, valor)
    editor.apply()
}


fun Context.save(llave: String, valor: Int) {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences),
            Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putInt(llave, valor)
    editor.apply()
}


fun Context.saveEncryptedData(key: String, value: String) {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences),
        Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(key, AESCrypt.encrypt(BuildConfig.KEY_SECRET,value))
    editor.apply()
}


fun Context.load(llave: String, defValue: String): String {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences), Context.MODE_PRIVATE)
    return sharedPreferences.getString(llave, defValue)!!
}

fun Context.load(llave: String, defValue: Boolean): Boolean {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences), Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean(llave, defValue)
}

fun Context.load(llave: String): String {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences), Context.MODE_PRIVATE)
    return sharedPreferences.getString(llave, "")!!
}

fun Context.loadEncryptedData(key: String): String {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences),
        Context.MODE_PRIVATE)
    return AESCrypt.decrypt(BuildConfig.KEY_SECRET,
        sharedPreferences.getString(key, ""))
}


fun <T> Context.load(classOfT: Class<T>): T {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences), Context.MODE_PRIVATE)
    val json = sharedPreferences.getString(getExtraCode(classOfT), "")
    return if (json!!.isNotEmpty()) {
        json.decrypt(classOfT)
    } else {
        fromJson("{}", classOfT)
    }
}

fun <T> Context.delete(classOfT: Class<T>) {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences), Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.remove(getExtraCode(classOfT))
    editor.apply()
}


fun Context.delete(llave: String) {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences), Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.remove(llave)
    editor.apply()
}