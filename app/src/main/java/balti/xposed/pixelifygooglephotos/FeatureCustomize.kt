package balti.xposed.pixelifygooglephotos

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import balti.xposed.pixelifygooglephotos.Constants.PREF_SPOOF_FEATURES_LIST
import balti.xposed.pixelifygooglephotos.Constants.SHARED_PREF_FILE_NAME

/**
 * Provides granular controls for selecting some specific feature flags.
 */
class FeatureCustomize: AppCompatActivity(R.layout.feature_customize) {

    private val pref by lazy {
        getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_WORLD_READABLE)
    }

    /**
     * Set of feature names enabled by the user, fetched from shared prefs.
     * If nothing is enabled, use default case from [DeviceProps.defaultFeatures].
     */
    private val enabledFeaturesNames: Set<String> by lazy {

        val defaultFeatures = DeviceProps.defaultFeatures
        val defaultFeatureLevelsName = defaultFeatures.map { it.displayName }.toSet()

        pref.getStringSet(PREF_SPOOF_FEATURES_LIST, defaultFeatureLevelsName)?: setOf()
    }

    private val utils by lazy { Utils() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val checkboxHolder = findViewById<LinearLayout>(R.id.feature_checkbox_holder)
        val saveButton = findViewById<Button>(R.id.save_features)

        /**
         * Populate checkboxes in for each feature name.
         */
        DeviceProps.allFeatures.withIndex().forEach {

            val checkBox = CheckBox(this)
            checkBox.apply {
                text = it.value.displayName
                id = it.index + 1
                isChecked = text in enabledFeaturesNames
            }

            checkboxHolder.addView(checkBox)

        }

        /**
         * When "Save" button is pressed, store the selected checkbox values in shared prefs.
         * Then close the activity and send RESULT_OK
         */
        saveButton.setOnClickListener {

            val checkedFeatureNames =
                checkboxHolder.children.filter { it is CheckBox && it.isChecked }
                    .map { (it as CheckBox).text.toString() }.toSet()

            pref.edit().apply {
                putStringSet(PREF_SPOOF_FEATURES_LIST, checkedFeatureNames)
                apply()
            }

            setResult(Activity.RESULT_OK)
            finish()

        }
    }

}