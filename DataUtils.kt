package com.clarocolombia.miclaro.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.*
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.clarocolombia.miclaro.R
import com.clarocolombia.miclaro.activities.Splash
import com.clarocolombia.miclaro.application.ClaroApplication
import com.clarocolombia.miclaro.empresas.dialogs.EmpAlertDialog
import com.clarocolombia.miclaro.empresas.dialogs.EmpAlertDialogModel
import com.clarocolombia.miclaro.home.fragments.HogarFragment
import com.clarocolombia.miclaro.models.local.Adbid
import com.clarocolombia.miclaro.models.local.LoginModel
import com.clarocolombia.miclaro.models.post.PostCuenta
import com.clarocolombia.miclaro.models.postEmp.UsuarioEmp
import com.clarocolombia.miclaro.models.response.ActualizarApp
import com.clarocolombia.miclaro.models.response.SesionModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.scottyab.aescrypt.AESCrypt
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.io.IOException
import java.io.InputStream
import java.math.RoundingMode
import java.nio.charset.Charset
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Created by Jefferson Rojas on 16/06/17.
 */

/* Datos compartidos - Conversiones */

abstract class funcs {
    interface events {
        fun click(pos: Int)
    }
}

fun String?.fromByteToGigabyte(): Double {
    return try {
        (((this?.toDouble() ?: 0.toDouble()) / 1024) / 1024) / 1024
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().log("String.fromByteToGigabyte = $this")
        FirebaseCrashlytics.getInstance().recordException(e)
        0.toDouble()
    }
}

fun String.terminosCondiciones(context : Context): String{
    return try {
        val actualizarApp = context.load(ActualizarApp::class.java)
        val url = actualizarApp.urlsClaro.firstOrNull { it.name == "terminos_by_tag" }?.url ?: ""
        url.replace("{tag}", this)
    }catch (e:Exception){
        ""
    }
}
var adbid: Adbid = Adbid()
fun Context.encrypt(adbi: Adbid): String {
    try {
        adbid = adbi
        val key = generateKey(adbid.password)
        val c = Cipher.getInstance(adbid.AES)
        c.init(Cipher.ENCRYPT_MODE, key)
        val encVal = c.doFinal(adbid.uID.toByteArray())
        val encryptValue = Base64.encodeToString(encVal, Base64.DEFAULT)

        val claroApplication = this.applicationContext
        return if (claroApplication is ClaroApplication) {
            encryptValue
        } else
            ""
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().setCustomKey(getString(R.string.adbid_error), e.message?:"")
        return ""
    }
}

private fun generateKey(password: String): SecretKeySpec {
    val digest = MessageDigest.getInstance(adbid.sha256)
    val charset = Charsets.UTF_8
    val bytes = password.toByteArray(charset)
    digest.update(bytes, 0, bytes.size)
    val key = digest.digest()
    return SecretKeySpec(key, adbid.AES)
}

/* Datos compartidos - Conversiones */
fun Double?.fromByteToGigabyte(): Double {
    return try {
        (((this ?: 0.toDouble()) / 1024) / 1024) / 1024
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().log("String.fromByteToGigabyte = $this")
        FirebaseCrashlytics.getInstance().recordException(e)
        0.toDouble()
    }
}


fun String?.fromByteToMegabyte(): Double {
    return try {
        (((this?.toDouble() ?: 0.toDouble()) / 1024) / 1024)
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().log("String.fromByteToGigabyte = $this")
        FirebaseCrashlytics.getInstance().recordException(e)
        0.toDouble()
    }
}

fun Double?.fromByteToMegabyte(): Double {
    return try {
        (((this ?: 0.toDouble()) / 1024) / 1024)
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().log("String.fromByteToGigabyte = $this")
        FirebaseCrashlytics.getInstance().recordException(e)
        0.toDouble()
    }
}

fun Double?.fromKbByteToMegabyte(): Double {
    return try {
        (((this ?: 0.toDouble()) / 1024))
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().log("String.fromByteToGigabyte = $this")
        FirebaseCrashlytics.getInstance().recordException(e)
        0.toDouble()
    }
}

fun String?.fromMegabyteToGigabyte(): Double {
    return try {
        (((this?.toDouble() ?: 0.toDouble()) / 1024))
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().log("String.fromByteToGigabyte = $this")
        FirebaseCrashlytics.getInstance().recordException(e)
        0.toDouble()
    }
}


fun getJson(@StringRes fileNameString: Int, context: Context): String {
    var json = ""
    try {
        val inputStream = context.assets.open("json/" + context.getString(fileNameString) + ".json")
        json = inputStream.readTextAndClose()
    } catch (ex: IOException) {
        ex.printStackTrace()
        return json
    }

    return json

}

fun InputStream.readTextAndClose(charset: Charset = Charsets.UTF_8): String {
    return this.bufferedReader(charset).use { it.readText() }
}

fun String?.fromGigabyteToByte(): Double {
    return try {
        ((this?.toDouble() ?: 0.toDouble() * 1024) * 1024) * 1024
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().log("String.fromByteToGigabyte = $this")
        FirebaseCrashlytics.getInstance().recordException(e)
        0.toDouble()
    }
}

fun Iterable<String>.toCSV(): String = toJson(this).replace("[", "").replace("]", "").replace("\"", "")

fun toJson(any: Any): String = Gson().toJson(any)

//fun <T> copy(any: Any, classOfT: Class<T>): T = Gson().fromJson(toJson(any), classOfT)

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
            return it?.invoke(null).toString()
        }
    }

    return classOfT.name
}

fun <T> fromIntent(intent: Intent, classOfT: Class<T>): T {
    val extraCode = getExtraCode(classOfT)
    val json = intent.extras?.getString(extraCode, "{}") ?: "{}"
    return fromJson(json, classOfT)
}

fun <T> fromIntent(intent: Intent, clazz: Class<Array<T>>): List<T> {
    val extraCode = getExtraCode(clazz.componentType!!)
    val json = intent.extras?.getString(extraCode, "[]")
    return Gson().fromJson(json, clazz).toList()
}

fun <T> Fragment.get(classOfT: Class<T>): T {
    val extraCode = getExtraCode(classOfT)
    val json = this.arguments?.getString(extraCode, "{}") ?: "{}"
    return fromJson(json, classOfT)
}

fun <T> Fragment.get(clazz: Class<Array<T>>): List<T> {
    val extraCode = getExtraCode(clazz.componentType!!)
    val json = this.arguments?.getString(extraCode, "[]")
    return Gson().fromJson(json, clazz).toList()
}

fun Fragment.put(any: Any) {
    val arguments = this.arguments ?: Bundle()
    arguments.putString(getExtraCode(any.javaClass), toJson(any))
    this.arguments = arguments
}

fun Intent.put(any: Any) {
    this.putExtra(any::class.java.name, toJson(any))
}

fun <T> Activity.get(classOfT: Class<T>): T {
    val json = this.intent.extras?.getString(classOfT.name, "{}") ?: "{}"
    return fromJson(json, classOfT)
}

fun <T : ViewDataBinding> Activity.bindView(layoutId: Int): T = DataBindingUtil.setContentView(this, layoutId)

fun haveError(jsonObject: JsonObject): Boolean = jsonObject.get("error").asInt != 0

fun getIntError(jsonObject: JsonObject): Int = jsonObject.get("error").asInt

fun getError(jsonObject: JsonObject): String {
    val jsonElement = jsonObject.get("response")
    if (jsonElement.isJsonArray || jsonElement.isJsonObject) {
        return jsonElement.toString()
    }
    return jsonElement.asString
}

fun getResponse(jsonObject: JsonObject): String = getError(jsonObject)

fun toPrice(price: Float): String = toPrice(String.format("%.0f", price))

fun toPrice(text: String?): String {
    if (text.isNullOrEmpty()) return "$ 0"
    val sym = DecimalFormatSymbols.getInstance()
    sym.groupingSeparator = '.'
    return try {
        DecimalFormat("$ ###,###", sym).format(text.replace(",", ".").toFloat())
    } catch (e: Exception) {
        ""
    }
}

fun toPrice2(text: String?): String {
    if (text.isNullOrEmpty()) return "$0"
    val sym = DecimalFormatSymbols.getInstance()
    sym.groupingSeparator = '.'
    return try {
        DecimalFormat("$###,###", sym).format(text.replace(",", ".").toFloat())
    } catch (e: Exception) {
        ""
    }
}

fun toPriceNew(price: Double) : String {
    val nf = NumberFormat.getNumberInstance(Locale.getDefault())
    nf.maximumFractionDigits = 2
    nf.roundingMode = RoundingMode.FLOOR

    return "$ "+ try {
        nf.format(price)
    } catch(e : Exception) {
        "0"
    }
}

/**
 * text : contiene un numero de tipo #######.##
 * return $ ###.###.###,##
 */

fun toPriceNew(text: String?,digitsFract : Int? = 0): String {
    if (text.isNullOrEmpty()) return "$ 0"

    val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
    nf.maximumFractionDigits = 2
    digitsFract?.let {
        nf.minimumFractionDigits = it
    }
    nf.roundingMode = RoundingMode.FLOOR

    return "$ "+ try {
        nf.format(text.toDouble())
    } catch(e : Exception) {
        "0"
    }
}

fun String.limpiarNumero(): String {
    val sym = DecimalFormatSymbols.getInstance()
    sym.groupingSeparator = '.'
    val valor = this
    return try {
        DecimalFormat("#,###.###", sym).format(this.replace(",", ".").toFloat())
    } catch (_: Exception) {
        valor
    }
}

fun String?.formatNumber(n : Int) : String {
    if (isNullOrEmpty()) return "0"

    val format = DecimalFormat()
    format.maximumFractionDigits = n

    return try {
        format.format(this?.toDouble())
    } catch(e : Exception) {
        "0"
    }
}

fun formatNumber(format: String, text: String): String = String.format(format, text.replace(",", ".").toFloat())

val passwordRegex = Regex("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}\$")

fun getDocumentType(lineOfBusiness: String, documentTypeMaster: String): String {
    return if (lineOfBusiness == HogarFragment.LINE_OF_BUSINESS || lineOfBusiness == HogarFragment.LINE_OF_BUSINESS_DTH) {
        getHomeDocumentType(documentTypeMaster)
    } else {
        getMobileDocumentType(documentTypeMaster)
    }
}

val tildes = arrayOf("ä", "Ä", "ë", "Ë", "ï", "Ï", "ö", "Ö", "ü", "Ü", "á", "é", "í", "ó", "ú", "á",
        "é", "í", "ó", "ú", "Á", "É", "Í", "Ó", "Ú", "Â", "Ê", "Î", "Ô", "Û", "â", "ê", "î", "ô", "û",
        "à", "è", "ì", "ò", "ù", "À", "È", "Ì", "Ò", "Ù", "ñ", "Ñ")

fun stringContainsItemFromList(inputStr: String, items: Array<String> = tildes): Boolean {
    for (i in items.indices) {
        if (inputStr.contains(items[i])) {
            return true
        }
    }
    return false
}

fun getHomeDocumentType(documentTypeMaster: String) = when (documentTypeMaster) {
    "1" -> "CC"
    "2" -> "CE"
    "3" -> "PP"
    "4" -> "CD"
    "5" -> "Nit"
    else -> documentTypeMaster
}

fun getMobileDocumentType(documentTypeMaster: String) = when (documentTypeMaster) {
    "1" -> "1"
    "2" -> "4"
    "3" -> "3"
    "4" -> "-1"
    else -> documentTypeMaster
}

fun Any.encrypt(): String = AESCrypt.encrypt(this::class.java.name, toJson(this))

fun <T> String.decrypt(classOfT: Class<T>): T {
    val json = AESCrypt.decrypt(classOfT.name, this)
    return fromJson(json, classOfT)
}

fun Context.save(data: Any) {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences), Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(getExtraCode(data.javaClass), data.encrypt())
    editor.apply()
}

fun Context.saveEmp(data: Any) {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences_emp), Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(getExtraCode(data.javaClass), data.encrypt())
    editor.apply()
}

/** Guarda UID para la sesion **/
fun Context.saveUID(data: Any) {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences_uid), Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(getExtraCode(data.javaClass), data.encrypt())
    editor.apply()
}

/**
 * Se guarda la autenticacion con huella
 * Se guarda los datos del orden de los elementos recyclers
 * */
fun Context.saveEmpGeneral(data: Any) {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences_emp_fingerprint), Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(getExtraCode(data.javaClass), data.encrypt())
    editor.apply()

}
fun Context.saveEmpGeneral(llave: String, valor: String) {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences_emp_fingerprint), Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(llave, valor)
    editor.apply()
}

fun Context.save(llave: String, valor: String) {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences), Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(llave, valor)
    editor.apply()
}

fun Context.save(llave: String, valor: Boolean) {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences), Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean(llave, valor)
    editor.apply()
}

fun Context.saveCache(llave: String, valor: String) {
    val sharedPreferences = this.getSharedPreferences("Cache", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(llave, valor)
    editor.apply()
}

fun Context.saveHe(llave: String, valor: String) {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences_he), Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(llave, valor)
    editor.apply()
}

fun Context.saveEmp(llave: String, valor: String) {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences_emp), Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(llave, valor)
    editor.apply()
}

fun Context.loadHe(llave: String): String {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences_he), Context.MODE_PRIVATE)
    return sharedPreferences.getString(llave, "{}") ?: "{}"
}

fun Context.load(llave: String): String {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences), Context.MODE_PRIVATE)
    return sharedPreferences.getString(llave, "") ?: ""
}

fun Context.load(llave: String,default: Boolean): Boolean {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences), Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean(llave, default)
}

fun Context.loadEmpGeneral(llave: String): String {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences_emp_fingerprint), Context.MODE_PRIVATE)
    return sharedPreferences.getString(llave, "") ?: ""
}

fun Context.loadCache(llave: String): String {
    val sharedPreferences = this.getSharedPreferences("Cache", Context.MODE_PRIVATE)
    return sharedPreferences.getString(llave, "") ?: ""
}

fun Context.loadEmp(llave: String): String {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences_emp), Context.MODE_PRIVATE)
    return sharedPreferences.getString(llave, "") ?: ""
}

fun Context.clear() {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences), Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.clear()
    editor.apply()
}

fun Context.clearCache() {
    val sharedPreferences = this.getSharedPreferences("Cache", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.clear()
    editor.apply()
}

fun Context.clearEmp() {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences_emp), Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.clear()
    editor.apply()
}

fun Context.clearEmpFingerprint() {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences_emp_fingerprint), Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.clear()
    editor.apply()
}

fun <T> Context.load(classOfT: Class<T>): T {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences), Context.MODE_PRIVATE)
    val json = sharedPreferences.getString(getExtraCode(classOfT), "") ?: ""
    return if (json.isNotEmpty()) {
        json.decrypt(classOfT)
    } else {
        fromJson("{}", classOfT)
    }
}

fun <T> Context.loadEmp(classOfT: Class<T>): T {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences_emp), Context.MODE_PRIVATE)
    val json = sharedPreferences.getString(getExtraCode(classOfT), "") ?: ""
    return if (json.isNotEmpty()) {
        json.decrypt(classOfT)
    } else {
        fromJson("{}", classOfT)
    }
}

fun <T> Context.loadEmpGeneral(classOfT: Class<T>): T {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences_emp_fingerprint), Context.MODE_PRIVATE)
    val json = sharedPreferences.getString(getExtraCode(classOfT), "") ?: ""
    return if (json.isNotEmpty()) {
        json.decrypt(classOfT)
    } else {
        fromJson("{}", classOfT)
    }
}

/** recupera UID para la sesion **/
fun <T> Context.loadUID(classOfT: Class<T>): T {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences_uid), Context.MODE_PRIVATE)
    val json = sharedPreferences.getString(getExtraCode(classOfT), "")?:""
    return if (json.isNotEmpty())
        json.decrypt(classOfT)
    else
        fromJson("{}", classOfT)
}

fun <T> Context.delete(classOfT: Class<T>) {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences), Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.remove(getExtraCode(classOfT))
    editor.apply()
}

fun Context.deleteKey(llave: String){
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences), Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.remove(llave)
    editor.apply()
}

fun Context.anyPostCuenta(): PostCuenta = this.load(SesionModel::class.java).cuentas.firstOrNull()
        ?: PostCuenta(token = this.load(ActualizarApp::class.java).token ?: "")

fun String.capitalize() = if (this.isNotEmpty()) this.substring(0, 1).toUpperCase() + this.substring(1).toLowerCase() else this

fun String.timeNow(): String {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat(this, Locale.getDefault())
    return dateFormat.format(calendar.time)
}

fun String.toCalendar(format: String): Calendar {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    calendar.time = dateFormat.parse(this)
    return calendar
}

fun String.isEmail(): Boolean = Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.hash256(): String {
    val bytes = this.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}

/** Andres David */

fun AppCompatActivity.isTimeOutEmp(isOut: Boolean, showMessage: Boolean): Boolean {
    val loginModel: LoginModel = loadEmp(LoginModel::class.java)
    val timeNow: String = Calendar.getInstance().time.time.toString()

    try {
        val cantSeconds: Int = ((timeNow.toLong() - loginModel.tiempo.toLong()) / 1000).toInt()

        if (cantSeconds >= 60 * 5) {//5 minutos TODO cambiar a 5 minutos
            clearEmp()
            if (isOut) {
                if (showMessage) {
                    val alertDialog = EmpAlertDialog()
                    alertDialog.show(this, EmpAlertDialogModel(
                            mensaje = "Se te ha agotado el tiempo de sesión",
                            textoBtnAceptar = "Aceptar",
                            icono = R.drawable.emp_ic_informacion
                    ))
                    alertDialog.onClickAceptar = {
                        Splash.startActivity(this)
                    }
                } else {
                    Splash.startActivity(this)
                }
            }
            return true
        } else {//si no ha cumplido el tiempo limite de sesion y entra a esta funcion -isTimeOutEmp- se reinicia el contador. Es tiempo de inactividad
            loginModel.tiempo = Calendar.getInstance().time.time.toString()
            saveEmp(loginModel)
        }
    } catch (_: NumberFormatException) {
    }

    return false
}

fun Context.isTransaccional(isMessage: Boolean, usuarioEmp: UsuarioEmp): Boolean {
    val rta = usuarioEmp.roleID == "1" && usuarioEmp.tipoClienteID == "2"
    if (!rta && isMessage) {
        Toast.makeText(this, "Sin permisos para esta acción", Toast.LENGTH_SHORT).show()
    }
    return rta
}

fun String.toSpanned(): Spanned {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        return Html.fromHtml(this)
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun AppCompatActivity.isLogin(): Boolean {
    val loginModel: LoginModel = loadEmp(LoginModel::class.java)
    return loginModel.user.isNotEmpty() && loginModel.password.isNotEmpty()
}


fun Context.convertDpToPixel(dp: Float): Int {
    return (dp * (this.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

fun String.toMd5(): String = String(Hex.encodeHex(DigestUtils.md5(this)))

/**
 * metodo creado para ocultar los iconos del textview y dejar solo el texto
 * @param textview = textView al cual se le debe ocultar los iconos
 */
fun ocultarIconosTextView(textview: TextView) {
    textview.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
}

fun Context.delete(llave: String) {
    val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_preferences), Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.remove(llave)
    editor.apply()
}

fun Activity.toast(msm: String, time: Int = Toast.LENGTH_SHORT){
    Toast.makeText(this, msm, time).show()
}


/**
 * Cambiar el color de una palabra o frase que especifiques
 * @param concatenarPalabra la palabra o frase a pintar la puedes concatenar
 * @param palabraAPintar palabra o frase que quieres colorear
 * @param color color que le quieres poner al text especificado
 **/
fun String.pintarPalabraEnTexto(palabraAPintar:String, color:Int,
                                concatenarPalabra: Boolean=false): SpannableStringBuilder {
    val textoCompleto = if(concatenarPalabra)
        (this+palabraAPintar)
    else
        this
    val spannable: Spannable = SpannableString(textoCompleto)
    val str = spannable.toString()
    val iStart = str.indexOf(palabraAPintar)
    val iEnd = iStart + palabraAPintar.length
    val nuevoTexto = SpannableStringBuilder(textoCompleto)
    nuevoTexto.setSpan(ForegroundColorSpan(color), iStart, iEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return nuevoTexto
}

fun String.hidePartEmail():String{
    if(!this.contains("@"))
        return this
    var nameEmail = this.split("@")[0]
    if(nameEmail.length > 2)
        nameEmail = nameEmail.substring(0, nameEmail.length - 2)
    else
        return "**********$this"
    return  this.replace(nameEmail,"**********")
}
/**
 * Convierte text a string HTML
 * */
fun String.toHtmlText() : Spanned {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun String?.encrypteNumberLine(): String {
    return when {
        this == null -> ""
        this.isEmpty() -> ""
        this.length >= 4 -> {
            val textLast = this.substring(this.length - 4, this.length)
            "*******$textLast"
        }
        else -> {
            this
        }
    }
}

