package balti.xposed.pixelifygooglephotos

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import balti.xposed.pixelifygooglephotos.Constants.PREF_STRICTLY_CHECK_GOOGLE_PHOTOS
import balti.xposed.pixelifygooglephotos.Constants.PREF_USE_PIXEL_2016
import balti.xposed.pixelifygooglephotos.Constants.SHARED_PREF_FILE_NAME
import com.google.android.material.snackbar.Snackbar
import java.io.File

class ActivityMain: AppCompatActivity() {

    private val pref by lazy {
        getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE)
    }

    private fun showRebootSnack(){
        val rootView = findViewById<LinearLayout>(R.id.root_view_for_snackbar)
        Snackbar.make(rootView, R.string.please_reboot, Snackbar.LENGTH_SHORT).show()
    }

    /**
     * Change permissions on private data, shared_prefs directory and preferences file.
     * Otherwise XSharedPreference cannot read the file.
     * Solution inspired from:
     * https://github.com/rovo89/XposedBridge/issues/233
     * https://github.com/GravityBox/GravityBox/blob/0aec21792c218a48602a258fbb0ab1fcb1e9be0c/GravityBox/src/main/java/com/ceco/r/gravitybox/WorldReadablePrefs.java
     */
    @SuppressLint("SetWorldReadable")
    private fun fixPermissions() {
        val dataDirectory = File("/data/data/$packageName")
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
        val prefsFile = File(sharedPrefsFolder, "$SHARED_PREF_FILE_NAME.xml")
        prefsFile.apply {
            if (exists()) setReadable(true, false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val switchPixel2016 = findViewById<SwitchCompat>(R.id.pixel_2016_switch)
        val switchEnforceGooglePhotos = findViewById<SwitchCompat>(R.id.spoof_only_in_google_photos_switch)

        switchPixel2016.apply {
            isChecked = pref.getBoolean(PREF_USE_PIXEL_2016, false)
            setOnCheckedChangeListener { _, isChecked ->
                pref.edit().run {
                    putBoolean(PREF_USE_PIXEL_2016, isChecked)
                    apply()
                    showRebootSnack()
                }
            }
        }

        switchEnforceGooglePhotos.apply {
            isChecked = pref.getBoolean(PREF_STRICTLY_CHECK_GOOGLE_PHOTOS, false)
            setOnCheckedChangeListener { _, isChecked ->
                pref.edit().run {
                    putBoolean(PREF_STRICTLY_CHECK_GOOGLE_PHOTOS, isChecked)
                    apply()
                    showRebootSnack()
                }
            }
        }

        fixPermissions()
    }

    override fun onPause() {
        super.onPause()
        fixPermissions()
    }

}