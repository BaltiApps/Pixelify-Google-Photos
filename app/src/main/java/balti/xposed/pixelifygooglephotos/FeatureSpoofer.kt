package balti.xposed.pixelifygooglephotos

import android.util.Log
import balti.xposed.pixelifygooglephotos.Constants.PACKAGE_NAME_GOOGLE_PHOTOS
import balti.xposed.pixelifygooglephotos.Constants.PREF_SPOOF_FEATURES_LIST
import balti.xposed.pixelifygooglephotos.Constants.PREF_STRICTLY_CHECK_GOOGLE_PHOTOS
import balti.xposed.pixelifygooglephotos.Constants.SHARED_PREF_FILE_NAME
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage

class FeatureSpoofer: IXposedHookLoadPackage {

    /**
     * Actual class is not android.content.pm.PackageManager.
     * It is an abstract class which cannot be hooked.
     * Actual class found from stackoverflow:
     * https://stackoverflow.com/questions/66523720/xposed-cant-hook-getinstalledapplications
     */
    private val CLASS_APPLICATION_MANAGER = "android.app.ApplicationPackageManager"

    /**
     * Method hasSystemFeature(). Two signatures exist. We need to hook both.
     * https://developer.android.com/reference/android/content/pm/PackageManager#hasSystemFeature(java.lang.String)
     * https://developer.android.com/reference/android/content/pm/PackageManager#hasSystemFeature(java.lang.String,%20int)
     */
    private val METHOD_HAS_SYSTEM_FEATURE = "hasSystemFeature"

    /**
     * Simple message to log messages in xposed log as well as android log.
     */
    private fun log(message: String){
        XposedBridge.log("PixelifyGooglePhotos: $message")
        Log.d("PixelifyGooglePhotos", message)
    }

    /**
     * To read preference of user.
     */
    private val pref by lazy {
        XSharedPreferences("balti.xposed.pixelifygooglephotos", SHARED_PREF_FILE_NAME)
    }

    /**
     * This is the final list of features to spoof.
     * Gets the specific set of features to be enabled selected by the user.
     * Default case: uses all features from "Pixel 2016" to "Pixel 2020".
     */
    private val finalFeaturesToSpoof: List<String> by lazy {

        val defaultFeatures = DeviceProps.defaultFeatures
        val defaultFeatureLevelsName = defaultFeatures.map { it.displayName }.toSet()

        pref.getStringSet(PREF_SPOOF_FEATURES_LIST, defaultFeatureLevelsName)?.let { set ->

            val eligibleFeatures: List<DeviceProps.Features> =

                when {
                    set.isEmpty() -> {
                        log("Feature flags init: EMPTY SET")
                        listOf()
                    }
                    set == defaultFeatureLevelsName -> {
                        log("Feature flags init: DEFAULT SET")
                        defaultFeatures
                    }
                    else -> DeviceProps.allFeatures.filter { set.contains(it.displayName) }
                }

            val allFeatureFlags = ArrayList<String>(0)

            eligibleFeatures.forEach {
                allFeatureFlags.addAll(it.featureFlags)
            }

            log("Feature flags init: $allFeatureFlags")

            allFeatureFlags
        }?: listOf()
    }

    /**
     * If a feature needed for google photos is needed, i.e. features in [finalFeaturesToSpoof],
     * then set result of hooked method [METHOD_HAS_SYSTEM_FEATURE] as `true`.
     * Else don't set anything.
     *
     * The [METHOD_HAS_SYSTEM_FEATURE] will be hooked anyway, but if the arguments passed,
     * is in [finalFeaturesToSpoof], then set result as true.
     */
    private fun returnTrueForSpoofedFeature(param: XC_MethodHook.MethodHookParam?){
        val arguments = param?.args?.toList()

        var isFeatureToBeSpoofed = false
        arguments?.forEach {
            if (it.toString() in finalFeaturesToSpoof) isFeatureToBeSpoofed = true
        }

        if (isFeatureToBeSpoofed) param?.setResult(true)
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {

        /**
         * If user selects to never use this on any other app other than Google photos,
         * then check package name and return if necessary.
         */
        if (pref.getBoolean(PREF_STRICTLY_CHECK_GOOGLE_PHOTOS, true) &&
            lpparam?.packageName != PACKAGE_NAME_GOOGLE_PHOTOS) return

        log("Loaded ${lpparam?.packageName}")

        /**
         * Hook hasSystemFeature(String).
         */
        XposedHelpers.findAndHookMethod(
            CLASS_APPLICATION_MANAGER,
            lpparam?.classLoader,
            METHOD_HAS_SYSTEM_FEATURE, String::class.java,
            object: XC_MethodHook() {

                override fun beforeHookedMethod(param: MethodHookParam?) {
                    super.beforeHookedMethod(param)
                    returnTrueForSpoofedFeature(param)
                }

            }
        )

        /**
         * Hook hasSystemFeature(String, int).
         */
        XposedHelpers.findAndHookMethod(
            CLASS_APPLICATION_MANAGER,
            lpparam?.classLoader,
            METHOD_HAS_SYSTEM_FEATURE, String::class.java, Int::class.java,
            object: XC_MethodHook() {

                override fun beforeHookedMethod(param: MethodHookParam?) {
                    super.beforeHookedMethod(param)
                    returnTrueForSpoofedFeature(param)
                }

            }
        )

    }
}