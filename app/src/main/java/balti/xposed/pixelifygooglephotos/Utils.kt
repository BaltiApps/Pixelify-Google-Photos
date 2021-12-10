package balti.xposed.pixelifygooglephotos

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import balti.xposed.pixelifygooglephotos.Constants.PREF_DEVICE_TO_SPOOF
import balti.xposed.pixelifygooglephotos.Constants.PREF_LAST_VERSION
import balti.xposed.pixelifygooglephotos.Constants.PREF_OVERRIDE_ROM_FEATURE_LEVELS
import balti.xposed.pixelifygooglephotos.Constants.PREF_SPOOF_FEATURES_LIST
import balti.xposed.pixelifygooglephotos.Constants.PREF_STRICTLY_CHECK_GOOGLE_PHOTOS
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter


/**
 * Utilities class for various functions.
 */
class Utils {

    /**
     * Used to force close an app.
     *
     * Uses root to stop an application.
     *
     * Tried my level best to use xposed API to force stop the application,
     * but it kept throwing error that XposedHelpers not found. No idea why.
     */
    fun forceStopPackage(packageName: String, context: Context){
        try {
            Toast.makeText(context, R.string.killing_please_wait, Toast.LENGTH_SHORT).show()
            Runtime.getRuntime().exec("su").apply {
                BufferedWriter(OutputStreamWriter(this.outputStream)).run {
                    this.write("am force-stop $packageName\n")
                    this.write("exit\n")
                    this.flush()
                }
            }
        } catch (e: Exception){
            Toast.makeText(context, R.string.failed_to_stop_package, Toast.LENGTH_SHORT).show()
            val intent = Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", packageName, null)
            }
            context.startActivity(intent)
        }

        /*
        * This could should have theoretically worked:
        *
        * val activityManager: ActivityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        * XposedHelpers.callMethod(activityManager, "forceStopPackage", packageName)
        */
    }

    /**
     * Launch an app.
     */
    fun openApplication(packageName: String, context: Context){
        try {
            val pm = context.packageManager
            val launchIntent = pm.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                context.startActivity(launchIntent)
            }
        }
        catch (e: Exception){
            Toast.makeText(context, R.string.failed_to_launch_package, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Change permissions on private data, shared_prefs directory and preferences file.
     * Otherwise XSharedPreference cannot read the file.
     * Solution inspired from:
     * https://github.com/rovo89/XposedBridge/issues/233
     * https://github.com/GravityBox/GravityBox/blob/0aec21792c218a48602a258fbb0ab1fcb1e9be0c/GravityBox/src/main/java/com/ceco/r/gravitybox/WorldReadablePrefs.java
     */
    /*@SuppressLint("SetWorldReadable")
    fun fixPermissions(thisPackageName: String) {
        val dataDirectory = File("/data/data/$thisPackageName")
        dataDirectory.apply {
            setExecutable(true, false)
            setReadable(true, false)
        }
        val sharedPrefsFolder = File(dataDirectory, "shared_prefs")
        sharedPrefsFolder.apply {
            if (exists()){
                setExecutable(true, false)
                setReadable(true, false)
            }
        }
        val prefsFile = File(sharedPrefsFolder, "${Constants.SHARED_PREF_FILE_NAME}.xml")
        prefsFile.apply {
            if (exists()) setReadable(true, false)
        }
    }*/

    /**
     * Write all keys of shared preference in a file as a JSON string.
     *
     * @param context Activity context
     * @param uri Uri of file to write to.
     * Using uri as it can be used to write a file in internal cache directory,
     * as well as an external location opened using [Intent.ACTION_CREATE_DOCUMENT].
     * @param pref SharedPreference instance.
     */
    fun writeConfigFile(context: Context, uri: Uri, pref: SharedPreferences?) {

        // List of keys from shared preference which need not be copied to file.
        // Or copied later like PREF_SPOOF_FEATURES_LIST.
        val fieldsNotToCopy = listOf(PREF_LAST_VERSION, PREF_SPOOF_FEATURES_LIST)

        val outputStream = context.contentResolver.openOutputStream(uri)
        val writer = BufferedWriter(OutputStreamWriter(outputStream))

        val jsonObject = JSONObject()
        pref?.all?.let { allPrefs ->
            for (key in allPrefs.keys){
                if (key !in fieldsNotToCopy) jsonObject.put(key, allPrefs[key])
            }
        }

        // Store PREF_SPOOF_FEATURES_LIST
        pref?.getStringSet(PREF_SPOOF_FEATURES_LIST, setOf())?.let {
            jsonObject.put(PREF_SPOOF_FEATURES_LIST, JSONArray(it.toTypedArray()))
        }

        writer.run {
            write(jsonObject.toString(4))
            close()
        }
    }

}