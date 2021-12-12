package balti.xposed.pixelifygooglephotos

import android.app.Activity
import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import balti.xposed.pixelifygooglephotos.Constants.PREF_ENABLE_VERBOSE_LOGS
import balti.xposed.pixelifygooglephotos.Constants.SHARED_PREF_FILE_NAME

class AdvancedOptionsActivity: AppCompatActivity(R.layout.advanced_options_activity) {

    private val pref by lazy {
        getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_WORLD_READABLE)
    }

    /**
     * This will send [Activity.RESULT_OK] to caller activity.
     * To be called when any option in this activity page is changed.
     * Example in [CheckBox.setPreferenceState].
     */
    private fun setResultOk(){
        setResult(Activity.RESULT_OK)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val verboseLogging = findViewById<CheckBox>(R.id.verbose_logging)

        verboseLogging.setPreferenceState(PREF_ENABLE_VERBOSE_LOGS, false)
    }

    /**
     * Extension function on [CheckBox].
     * Set initial value from SharedPreference.
     * Modifies value in SharedPreference when any change in Checkbox state.
     *
     * @param preferenceKey Get value from this key in SharedPreference.
     * @param defaultValue Value to set in Checkbox if no value is present
     * for key [preferenceKey] in SharedPreference.
     */
    fun CheckBox.setPreferenceState(preferenceKey: String, defaultValue: Boolean){
        isChecked = pref.getBoolean(preferenceKey, defaultValue)
        setOnCheckedChangeListener { _, isChecked ->
            setResultOk()
            pref.edit().run {
                putBoolean(preferenceKey, isChecked)
                apply()
            }
        }
    }

}