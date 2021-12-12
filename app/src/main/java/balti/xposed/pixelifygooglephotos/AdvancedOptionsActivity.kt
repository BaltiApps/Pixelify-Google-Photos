package balti.xposed.pixelifygooglephotos

import android.app.Activity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import balti.xposed.pixelifygooglephotos.Constants.PREF_DEVICE_TO_SPOOF
import balti.xposed.pixelifygooglephotos.Constants.PREF_ENABLE_VERBOSE_LOGS
import balti.xposed.pixelifygooglephotos.Constants.PREF_SPOOF_ANDROID_VERSION_FOLLOW_DEVICE
import balti.xposed.pixelifygooglephotos.Constants.PREF_SPOOF_ANDROID_VERSION_MANUAL
import balti.xposed.pixelifygooglephotos.Constants.SHARED_PREF_FILE_NAME

class AdvancedOptionsActivity: AppCompatActivity(R.layout.advanced_options_activity) {

    private val pref by lazy {
        getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_WORLD_READABLE)
    }

    private val verboseLogging by lazy { findViewById<CheckBox>(R.id.verbose_logging) }
    private val deviceNameLabel by lazy { findViewById<TextView>(R.id.device_name_label) }
    private val androidVersionRadioGroup by lazy { findViewById<RadioGroup>(R.id.android_version_radio_group) }
    private val deviceAndroidVersion by lazy { findViewById<TextView>(R.id.device_android_version) }
    private val androidVersionSpinner by lazy { findViewById<Spinner>(R.id.android_version_spinner) }
    private val save by lazy { findViewById<Button>(R.id.save_advanced_option) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (pref == null) return

        verboseLogging.isChecked = pref.getBoolean(PREF_ENABLE_VERBOSE_LOGS, false)

        /**
         * Get the current spoofing device an its android version.
         */
        val deviceNameInPreference = pref.getString(PREF_DEVICE_TO_SPOOF, DeviceProps.defaultDeviceName)
        val spoofDevice = DeviceProps.getDeviceProps(deviceNameInPreference)
        deviceNameLabel.text = spoofDevice?.deviceName
        deviceAndroidVersion.text = spoofDevice?.androidVersion?.label

        /**
         * String list of all [DeviceProps.AndroidVersion.label].
         */
        val allVersionLabels = DeviceProps.allAndroidVersions.map { it.label }

        /**
         * Set spinner for manually setting android version.
         */
        androidVersionSpinner.apply {
            val aa = ArrayAdapter(
                this@AdvancedOptionsActivity,
                android.R.layout.simple_spinner_item,
                allVersionLabels
            )

            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            adapter = aa
            val defaultSelection = pref?.getString(PREF_SPOOF_ANDROID_VERSION_MANUAL, null)
            setSelection(aa.getPosition(defaultSelection))
        }

        /**
         * Set default radio button.
         */
        androidVersionRadioGroup.apply {

            // hide spinner if not to be manually set.
            setOnCheckedChangeListener { _, checkedId ->
                androidVersionSpinner.isVisible = checkedId == R.id.manually_set_android_version
            }

            val manualVersion = pref.getString(PREF_SPOOF_ANDROID_VERSION_MANUAL, null)?.trim()

            check(
                when {
                    pref.getBoolean(PREF_SPOOF_ANDROID_VERSION_FOLLOW_DEVICE, false) -> R.id.follow_spoof_device_version
                    manualVersion != null && manualVersion in allVersionLabels -> R.id.manually_set_android_version
                    else -> R.id.dont_spoof_android_version
                }
            )
        }

        save.setOnClickListener {
            savePreferences()
        }

    }

    /**
     * Function to save all options.
     */
    private fun savePreferences(){
        pref?.edit()?.run {

            /** Option for verbose log. */
            putBoolean(PREF_ENABLE_VERBOSE_LOGS, verboseLogging.isChecked)

            when(androidVersionRadioGroup.checkedRadioButtonId){

                /** If not to spoof, disable both preferences */
                R.id.dont_spoof_android_version -> {
                    putBoolean(PREF_SPOOF_ANDROID_VERSION_FOLLOW_DEVICE, false)
                    putString(PREF_SPOOF_ANDROID_VERSION_MANUAL, null)
                }

                /** Enable only [PREF_SPOOF_ANDROID_VERSION_FOLLOW_DEVICE]
                 * if to follow spoof device android version.
                 */
                R.id.follow_spoof_device_version -> {
                    putBoolean(PREF_SPOOF_ANDROID_VERSION_FOLLOW_DEVICE, true)
                    putString(PREF_SPOOF_ANDROID_VERSION_MANUAL, null)
                }

                /**
                 * If manually set, then get the label from spinner itself
                 * and set the value for preference [PREF_SPOOF_ANDROID_VERSION_MANUAL].
                 */
                R.id.manually_set_android_version -> {
                    putBoolean(PREF_SPOOF_ANDROID_VERSION_FOLLOW_DEVICE, false)
                    putString(
                        PREF_SPOOF_ANDROID_VERSION_MANUAL,
                        androidVersionSpinner.selectedItem.toString()
                    )
                }

            }

            apply()
        }
        setResult(Activity.RESULT_OK)
        finish()
    }

}