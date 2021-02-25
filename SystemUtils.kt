package com.wigilabs.progressus.utils

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.*
import android.os.Build
import android.provider.OpenableColumns
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.wigilabs.progressus.agricultor.agendaChatAgricultor.fragments.FragmentChatAgricultor.Companion.PERMISSIONS_REQUEST_CAMERA
import com.wigilabs.progressus.agricultor.agendaChatAgricultor.fragments.FragmentChatAgricultor.Companion.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
import io.grpc.internal.IoUtils
import save
import java.io.*
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*


fun ContentResolver.getFileName(fileUri: Uri): String {

    var name = ""
    val returnCursor = this.query(fileUri, null, null, null, null)
    returnCursor?.let{
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        it.moveToFirst()
        name = it.getString(nameIndex)
        it.close()
    }

    return name
}




fun registerNetworkCallback(context: Context) {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val builder = NetworkRequest.Builder()
    connectivityManager!!.registerNetworkCallback(
        builder.build(),
        object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                // Network Available
                context.save("isNetworkConnected",true)
            }


            override fun onLost(network: Network) {
                // Network Not Available
                context.save("isNetworkConnected",false)
            }
        }
    )

}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
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

fun openAppInGooglePlay(context: Context,urlApp: String){
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(urlApp)
        setPackage("com.android.vending")
    }
    context.startActivity(intent)
}



/**
 * Metodo para convertir la primera letra en Mayuscula
 * */
fun String.firstCapitalLetter() : String {
    if(this.isEmpty())
        return this
    val strArray = this.trim().toLowerCase(Locale.getDefault())
        .split(" ").toTypedArray()
    val builder = StringBuilder()
    for (s in strArray) {
        val cap = s.substring(0,1)
            .toUpperCase(Locale.getDefault())+ s.substring(1)
        builder.append("$cap ")
    }
    return builder.toString()
}



/**
* Convierte text a string HTML
* */
fun String.toHtmlText() : Spanned {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
}



/**
 * Cambiar el color de una palabra o frase que especifiques
 * @param textToChange text que quieres colorear
 * @param color color que le quieres poner al text especificado
 **/
fun String.changeColorOfText(textToChange:String, color:Int):SpannableStringBuilder {
    val spannable: Spannable = SpannableString(this)
    val str = spannable.toString()
    val iStart = str.indexOf(textToChange)
    val iEnd = iStart + textToChange.length
    val ssText = SpannableStringBuilder(this)
    ssText.setSpan(ForegroundColorSpan(color), iStart, iEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return ssText
}

/**
 * Valida que la sintaxis sea la de un correo
 **/
fun String?.checkEmail():Boolean{
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this?:"").matches()
}

fun String.clearText():String{
    return this.replace("'","")
        .replace("\"","").replace(",","")
}




/**
 *Este metodo convierte la fecha formato <yyyy-MM-dd'T'HH:mm> en formato
 * legible para el usuario <Dia Mes Año>
 *
 * Ejemplos de formatos
 * todo https://androidwave.com/format-datetime-in-android/
 * **/
fun Date.toReadableFormat(newFormat:String):String{
    return SimpleDateFormat(newFormat,Locale.getDefault()).format(this.time)
}


fun crearCopia(uriImage: Uri?, activity:Activity):File{
    val parcelFileDescriptor= activity.contentResolver.openFileDescriptor(uriImage!!,"r",null)
    var fileImagenProcesada = File(activity.cacheDir, activity.contentResolver.getFileName(uriImage))
    if(parcelFileDescriptor!=null) {
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(activity.cacheDir, activity.contentResolver.getFileName(uriImage))
        val outputStream = FileOutputStream(file)
        IoUtils.copy(inputStream, outputStream)
        fileImagenProcesada = procesarImagen(file)
    }
    return fileImagenProcesada
}


fun procesarImagen(file: File):File{
    var foto= BitmapFactory.decodeFile(file.absolutePath)
    val exifInterface = ExifInterface(file.absolutePath)
    val orientation= exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,1)
    val matrix= Matrix()
    when(orientation){
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
    }
    val maxSize = 600
    val outWidth:Int
    val outHeight:Int
    if(foto.width > foto.height){
        outWidth = maxSize
        outHeight = foto.height*maxSize/foto.width
    }else{
        outHeight = maxSize
        outWidth = foto.width*maxSize/foto.height
    }
    foto= Bitmap.createScaledBitmap(foto,outWidth,outHeight,true)
    foto= Bitmap.createBitmap(foto,0,0,foto.width, foto.height, matrix,true)
    val outputStream= BufferedOutputStream(FileOutputStream(file))
    foto.compress(Bitmap.CompressFormat.JPEG,50,outputStream)
    outputStream.flush()
    outputStream.close()
    foto.recycle()
    return file
}


/**
 * @param permissionCamera <true> si el permiso a pedir es para camara
 */
fun checkPermission(context: Context, permissionCamera:Boolean): Boolean {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if(permissionCamera){
            return if(ContextCompat.checkSelfPermission(context,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(context
                            as Activity, Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(context,
                        arrayOf(Manifest.permission.CAMERA),
                        PERMISSIONS_REQUEST_CAMERA)
                } else {
                    ActivityCompat.requestPermissions(context,
                        arrayOf(Manifest.permission.CAMERA),
                        PERMISSIONS_REQUEST_CAMERA)
                }
                false
            } else {
                true
            }
        }else{
            return if(ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(context
                            as Activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(context,
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                } else {
                    ActivityCompat.requestPermissions(context,
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                }
                false
            } else {
                true
            }
        }
    } else {
        return true
    }
}

fun isInternetAvailable(context: Context): Boolean {
    var result = false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }

            }
        }
    }
    return result
}




