package balti.xposed.pixelifygooglephotos

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import balti.xposed.pixelifygooglephotos.Constants.PREF_STRICTLY_CHECK_GOOGLE_PHOTOS
import balti.xposed.pixelifygooglephotos.Constants.PREF_USE_PIXEL_2016
import balti.xposed.pixelifygooglephotos.Constants.SHARED_PREF_FILE_NAME
import com.google.android.material.snackbar.Snackbar

class ActivityMain: AppCompatActivity() {

    private val pref by lazy {
        getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE)
    }

    private fun showRebootSnack(){
        val rootView = findViewById<LinearLayout>(R.id.root_view_for_snackbar)
        Snackbar.make(rootView, R.string.please_reboot, Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val switchPixel2016 = findViewById<SwitchCompat>(R.id.pixel_2016_switch)
        val switchEnforceGooglePhotos = findViewById<SwitchCompat>(R.id.spoof_only_in_google_photos_switch)

        switchPixel2016.setOnCheckedChangeListener { _, isChecked ->
            pref.edit().apply {
                putBoolean(PREF_USE_PIXEL_2016, isChecked)
                apply()
                showRebootSnack()
            }
        }

        switchEnforceGooglePhotos.setOnCheckedChangeListener { _, isChecked ->
            pref.edit().apply {
                putBoolean(PREF_STRICTLY_CHECK_GOOGLE_PHOTOS, isChecked)
                apply()
                showRebootSnack()
            }
        }
    }

}