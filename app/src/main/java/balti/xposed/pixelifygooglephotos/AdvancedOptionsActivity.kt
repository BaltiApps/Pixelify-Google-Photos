package balti.xposed.pixelifygooglephotos

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import balti.xposed.pixelifygooglephotos.Constants.PREF_ENABLE_VERBOSE_LOGS
import balti.xposed.pixelifygooglephotos.Constants.SHARED_PREF_FILE_NAME

class AdvancedOptionsActivity: AppCompatActivity(R.layout.advanced_options_activity) {

    private val pref by lazy {
        getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_WORLD_READABLE)
    }

    private val verboseLogging by lazy { findViewById<CheckBox>(R.id.verbose_logging) }
    private val save by lazy { findViewById<Button>(R.id.save_advanced_option) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (pref == null) return

        verboseLogging.isChecked = pref.getBoolean(PREF_ENABLE_VERBOSE_LOGS, false)

        save.setOnClickListener {
            savePreferences()
        }

    }

    /**
     * Function to save all options.
     */
    private fun savePreferences(){
        pref?.edit()?.run {

            putBoolean(PREF_ENABLE_VERBOSE_LOGS, verboseLogging.isChecked)

            apply()
        }
        setResult(Activity.RESULT_OK)
    }

}