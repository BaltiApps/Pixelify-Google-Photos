package balti.xposed.pixelifygooglephotos

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import balti.xposed.pixelifygooglephotos.Constants.PREF_DEVICE_TO_SPOOF
import balti.xposed.pixelifygooglephotos.Constants.PREF_SPOOF_FEATURES_LIST
import balti.xposed.pixelifygooglephotos.Constants.PREF_STRICTLY_CHECK_GOOGLE_PHOTOS
import balti.xposed.pixelifygooglephotos.Constants.PREF_USE_PIXEL_2016
import balti.xposed.pixelifygooglephotos.Constants.SHARED_PREF_FILE_NAME
import com.google.android.material.snackbar.Snackbar


class ActivityMain: AppCompatActivity(R.layout.activity_main) {

    private val pref by lazy {
        getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE)
    }

    private fun showRebootSnack(){
        val rootView = findViewById<ScrollView>(R.id.root_view_for_snackbar)
        Snackbar.make(rootView, R.string.please_force_stop_google_photos, Snackbar.LENGTH_SHORT).show()
    }

    /**
     * Animate the "Feature flags changed" textview and hide it after showing for sometime.
     */
    private fun peekFeatureFlagsChanged(textView: TextView){
        textView.run {
            alpha = 1.0f
            animate().alpha(0.0f).apply {
                duration = 1000
                startDelay = 3000
            }.start()
        }
    }

    private val utils by lazy { Utils() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Link to xml views.
         */
        val resetSettings = findViewById<Button>(R.id.reset_settings)
        val customizeFeatureFlags = findViewById<LinearLayout>(R.id.customize_feature_flags)
        val featureFlagsChanged = findViewById<TextView>(R.id.feature_flags_changed)
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
                putStringSet(
                    PREF_SPOOF_FEATURES_LIST,
                    DeviceProps.defaultFeatures.map { it.displayName }.toSet()
                )
                apply()
            }
            finish()
            startActivity(intent)
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
            /** Second argument is `false` to prevent calling [peekFeatureFlagsChanged] on initialization */
            setSelection(aa.getPosition(defaultSelection), false)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val deviceName = aa.getItem(position)
                    pref.edit().apply {
                        putString(PREF_DEVICE_TO_SPOOF, deviceName)
                        putStringSet(
                            PREF_SPOOF_FEATURES_LIST,
                            DeviceProps.getFeaturesUpToFromDeviceName(deviceName)
                        )
                        apply()
                    }

                    peekFeatureFlagsChanged(featureFlagsChanged)
                    showRebootSnack()
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