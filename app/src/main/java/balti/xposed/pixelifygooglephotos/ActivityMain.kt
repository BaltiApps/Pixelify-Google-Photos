package balti.xposed.pixelifygooglephotos

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import balti.xposed.pixelifygooglephotos.Constants.FIELD_LATEST_VERSION_CODE
import balti.xposed.pixelifygooglephotos.Constants.PREF_DEVICE_TO_SPOOF
import balti.xposed.pixelifygooglephotos.Constants.PREF_LAST_VERSION
import balti.xposed.pixelifygooglephotos.Constants.PREF_OVERRIDE_ROM_FEATURE_LEVELS
import balti.xposed.pixelifygooglephotos.Constants.PREF_SPOOF_FEATURES_LIST
import balti.xposed.pixelifygooglephotos.Constants.PREF_STRICTLY_CHECK_GOOGLE_PHOTOS
import balti.xposed.pixelifygooglephotos.Constants.RELEASES_URL
import balti.xposed.pixelifygooglephotos.Constants.RELEASES_URL2
import balti.xposed.pixelifygooglephotos.Constants.SHARED_PREF_FILE_NAME
import balti.xposed.pixelifygooglephotos.Constants.TELEGRAM_GROUP
import balti.xposed.pixelifygooglephotos.Constants.UPDATE_INFO_URL
import balti.xposed.pixelifygooglephotos.Constants.UPDATE_INFO_URL2
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.URL


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

    /**
     * Activity launcher for [FeatureCustomize] activity.
     * If user presses "Save" on [FeatureCustomize] activity, then result code is RESULT_OK.
     * Then show prompt to force stop Google Photos.
     */
    private val childActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                showRebootSnack()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Link to xml views.
         */
        val resetSettings = findViewById<Button>(R.id.reset_settings)
        val customizeFeatureFlags = findViewById<LinearLayout>(R.id.customize_feature_flags)
        val featureFlagsChanged = findViewById<TextView>(R.id.feature_flags_changed)
        val overrideROMFeatureLevels = findViewById<SwitchCompat>(R.id.override_rom_feature_levels)
        val switchEnforceGooglePhotos = findViewById<SwitchCompat>(R.id.spoof_only_in_google_photos_switch)
        val deviceSpooferSpinner = findViewById<Spinner>(R.id.device_spoofer_spinner)
        val forceStopGooglePhotos = findViewById<Button>(R.id.force_stop_google_photos)
        val openGooglePhotos = findViewById<ImageButton>(R.id.open_google_photos)
        val telegramLink = findViewById<TextView>(R.id.telegram_group)
        val updateAvailableLink = findViewById<TextView>(R.id.update_available_link)

        /**
         * Set default spoof device to [DeviceProps.defaultDeviceName].
         * Set check for google photos as `false`.
         * Set default feature levels to spoof.
         * Restart the activity.
         */
        resetSettings.setOnClickListener {
            pref.edit().run {
                putString(PREF_DEVICE_TO_SPOOF, DeviceProps.defaultDeviceName)
                putBoolean(PREF_OVERRIDE_ROM_FEATURE_LEVELS, true)
                putBoolean(PREF_STRICTLY_CHECK_GOOGLE_PHOTOS, true)
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
         * See [FeatureSpoofer.featuresNotToSpoof].
         */
        overrideROMFeatureLevels.apply {
            isChecked = pref.getBoolean(PREF_OVERRIDE_ROM_FEATURE_LEVELS, true)
            setOnCheckedChangeListener { _, isChecked ->
                pref.edit().run {
                    putBoolean(PREF_OVERRIDE_ROM_FEATURE_LEVELS, isChecked)
                    apply()
                    showRebootSnack()
                }
            }
        }

        /**
         * See [FeatureSpoofer].
         */
        switchEnforceGooglePhotos.apply {
            isChecked = pref.getBoolean(PREF_STRICTLY_CHECK_GOOGLE_PHOTOS, true)
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

        /**
         * Launch [FeatureCustomize] to fine select the features.
         */
        customizeFeatureFlags.setOnClickListener {
            childActivityLauncher.launch(Intent(this, FeatureCustomize::class.java))
        }

        /**
         * Open telegram group.
         */
        telegramLink.apply {
            paintFlags = Paint.UNDERLINE_TEXT_FLAG
            setOnClickListener {
                openWebLink(TELEGRAM_GROUP)
            }
        }

        /**
         * Check if changelogs need to be shown when upgrading from older version.
         */
        pref.apply {
            val thisVersion = BuildConfig.VERSION_CODE
            if (getInt(PREF_LAST_VERSION, 0) < thisVersion){
                showChangeLog()
                edit().apply {
                    putInt(PREF_LAST_VERSION, thisVersion)
                    apply()
                }
            }
        }

        /**
         * Check for updates in background thread.
         * Yes AsyncTask is deprecated, but it works fine and for such a short network operation
         * it is useless to try coroutine or something like that.
         */
        AsyncTask.execute {
            isUpdateAvailable()?.let { url ->
                runOnUiThread {
                    updateAvailableLink.apply {
                        paintFlags = Paint.UNDERLINE_TEXT_FLAG
                        visibility = View.VISIBLE
                        setOnClickListener {
                            openWebLink(url)
                        }
                    }
                }
            }
        }

        utils.fixPermissions(packageName)
    }

    /**
     * Method to show latest changes.
     */
    private fun showChangeLog(){
        AlertDialog.Builder(this)
            .setTitle(R.string.version_head)
            .setMessage(R.string.version_desc)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    /**
     * Populate menu.
     * Menu contains option to show changelog.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Click listener on menu.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_changelog -> showChangeLog()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Check if update is available. Return url string of Github Releases page if update is present.
     * Else returns null.
     *
     * This checks for update in two repositories.
     * The original BaltiApps repository, as well as LSPosed repository.
     * If update is available in any of them it send the respective repo's Release page link.
     */
    private fun isUpdateAvailable(): String? {

        fun getUpdateStatus(url: String): Boolean {
            var jsonString = ""
            val baos = ByteArrayOutputStream()

            /**
             * Get contents of the file into a string.
             */
            try {
                URL(url).openStream().use { input ->
                    baos.use { output ->
                        input.copyTo(output)
                    }
                    jsonString = baos.toString()
                }
            } catch (_: Exception) {
                return false
            }

            /**
             * Parse the string as a JSON object.
             */
            return if (jsonString.isNotBlank()) {
                try {
                    val json = JSONObject(jsonString)
                    val remoteVersion = json.getInt(FIELD_LATEST_VERSION_CODE)
                    BuildConfig.VERSION_CODE < remoteVersion
                } catch (_: Exception) {
                    false
                }
            } else false
        }

        /**
         * Check both repositories.
         */
        return when {
            getUpdateStatus(UPDATE_INFO_URL) -> RELEASES_URL
            getUpdateStatus(UPDATE_INFO_URL2) -> RELEASES_URL2
            else -> null
        }
    }

    /**
     * Open any url link
     */
    fun openWebLink(url: String){
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        })
    }

    override fun onPause() {
        super.onPause()
        utils.fixPermissions(packageName)
    }

}