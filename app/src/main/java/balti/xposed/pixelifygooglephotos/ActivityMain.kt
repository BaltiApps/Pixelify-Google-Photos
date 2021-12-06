package balti.xposed.pixelifygooglephotos

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import balti.xposed.pixelifygooglephotos.Constants.PREF_DEVICE_TO_SPOOF
import balti.xposed.pixelifygooglephotos.Constants.PREF_STRICTLY_CHECK_GOOGLE_PHOTOS
import balti.xposed.pixelifygooglephotos.Constants.PREF_USE_PIXEL_2016
import balti.xposed.pixelifygooglephotos.Constants.SHARED_PREF_FILE_NAME
import com.google.android.material.snackbar.Snackbar
import de.robv.android.xposed.XposedHelpers
import java.io.BufferedWriter
import java.io.File
import java.io.OutputStreamWriter

class ActivityMain: AppCompatActivity() {

    private val pref by lazy {
        getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE)
    }

    private fun showRebootSnack(){
        val rootView = findViewById<ScrollView>(R.id.root_view_for_snackbar)
        Snackbar.make(rootView, R.string.please_force_stop_google_photos, Snackbar.LENGTH_SHORT).show()
    }

    private val utils by lazy { Utils() }

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
        val deviceSpooferSpinner = findViewById<Spinner>(R.id.device_spoofer_spinner)
        val forceStopGooglePhotos = findViewById<Button>(R.id.force_stop_google_photos)
        val openGooglePhotos = findViewById<ImageButton>(R.id.open_google_photos)

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

        deviceSpooferSpinner.apply {
            val deviceNames = DeviceProps.allDevices.map { it.deviceName }
            val aa = ArrayAdapter(this@ActivityMain,android.R.layout.simple_spinner_item, deviceNames)

            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            adapter = aa
            val defaultSelection = pref.getString(PREF_DEVICE_TO_SPOOF, DeviceProps.defaultDeviceName)
            setSelection(aa.getPosition(defaultSelection))

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    pref.edit().apply {
                        putString(PREF_DEVICE_TO_SPOOF, aa.getItem(position))
                        apply()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        forceStopGooglePhotos.setOnClickListener {
            utils.forceStopPackage(Constants.PACKAGE_NAME_GOOGLE_PHOTOS, this)
        }

        openGooglePhotos.setOnClickListener {
            utils.openApplication(Constants.PACKAGE_NAME_GOOGLE_PHOTOS, this)
        }

        fixPermissions()
    }

    override fun onPause() {
        super.onPause()
        fixPermissions()
    }

}