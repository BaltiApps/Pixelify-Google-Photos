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
import android.content.Intent




class ActivityMain: AppCompatActivity(R.layout.activity_main) {

    private val pref by lazy {
        getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE)
    }

    private fun showRebootSnack(){
        val rootView = findViewById<ScrollView>(R.id.root_view_for_snackbar)
        Snackbar.make(rootView, R.string.please_force_stop_google_photos, Snackbar.LENGTH_SHORT).show()
    }

    private val utils by lazy { Utils() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Link to xml views.
         */
        val resetSettings = findViewById<Button>(R.id.reset_settings)
        val switchPixel2016 = findViewById<SwitchCompat>(R.id.pixel_2016_switch)
        val switchEnforceGooglePhotos = findViewById<SwitchCompat>(R.id.spoof_only_in_google_photos_switch)
        val deviceSpooferSpinner = findViewById<Spinner>(R.id.device_spoofer_spinner)
        val forceStopGooglePhotos = findViewById<Button>(R.id.force_stop_google_photos)
        val openGooglePhotos = findViewById<ImageButton>(R.id.open_google_photos)

        /**
         * Set default spoof device to [DeviceProps.defaultDeviceName].
         * Set other boolean values.
         * Restart the activity.
         */
        resetSettings.setOnClickListener {
            pref.edit().run {
                putString(PREF_DEVICE_TO_SPOOF, DeviceProps.defaultDeviceName)
                putBoolean(PREF_USE_PIXEL_2016, false)
                putBoolean(PREF_STRICTLY_CHECK_GOOGLE_PHOTOS, false)
                apply()
            }
            finish()
            startActivity(intent)
        }

        /**
         * See [FeatureSpoofer].
         */
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

        /**
         * See [FeatureSpoofer].
         */
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

        /**
         * See [DeviceSpoofer].
         */
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

        /**
         * See [Utils.forceStopPackage].
         */
        forceStopGooglePhotos.setOnClickListener {
            utils.forceStopPackage(Constants.PACKAGE_NAME_GOOGLE_PHOTOS, this)
        }

        /**
         * See [Utils.openApplication].
         */
        openGooglePhotos.setOnClickListener {
            utils.openApplication(Constants.PACKAGE_NAME_GOOGLE_PHOTOS, this)
        }

        utils.fixPermissions(packageName)
    }

    override fun onPause() {
        super.onPause()
        utils.fixPermissions(packageName)
    }

}