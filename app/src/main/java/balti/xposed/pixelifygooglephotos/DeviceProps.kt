package balti.xposed.pixelifygooglephotos

/**
 * Build values taken from:
 * Pixel 6:
 * https://github.com/DotOS/android_frameworks_base/blob/dot12/core/java/com/android/internal/util/custom/PixelPropsUtils.java
 * Pixel 2:
 * https://gist.github.com/markstachowski/4069f15c5c989827b9e64c0aec045434
 * Pixel 5a:
 * https://github.com/LineageOS/android_device_google_barbet/blob/lineage-18.1/lineage_barbet.mk
 * All other pixels:
 * https://github.com/orgs/Pixel-Props/repositories
 * Also from
 * https://github.com/DotOS/android_frameworks_base/commit/3f7ea7d070017ed1f38035333f084865865698b2
 *
 * Features taken from:
 * https://t.me/PixelProps
 * https://github.com/DotOS/android_vendor_dot/blob/dot12/prebuilt/common/etc/pixel_2016_exclusive.xml
 * https://github.com/ayush5harma/PixelFeatureDrops/tree/master/system/etc/sysconfig
 */
object DeviceProps {

    /**
     * Class to store different feature flags for different pixels.
     * @param displayName String to show to user to customize flag selection. Example "Pixel 2020"
     * Also note that these display names are what is actually stored in shared preferences.
     * The actual feature flags are then derived from the display names.
     * @param featureFlags List of actual features spoofed to Google Photos for that particular [displayName].
     * Example, for [displayName] = "Pixel 2020", [featureFlags] = listOf("com.google.android.feature.PIXEL_2020_EXPERIENCE")
     */
    class Features(
        val displayName: String,
        val featureFlags: List<String>,
    ){
        constructor(displayName: String, vararg featureFlags: String) : this(
            displayName,
            featureFlags.toList()
        )

        constructor(displayName: String, singleFeature: String) : this(
            displayName,
            listOf(singleFeature)
        )
    }

    /**
     * List of all possible feature flags.
     * CHRONOLOGY IS IMPORTANT. Elements are arranged in accordance of device release date.
     */
    val allFeatures = listOf(

        Features("Pixel",
            "com.google.android.feature.PIXEL_EXPERIENCE",
            "com.google.android.feature.GOOGLE_BUILD",
            "com.google.android.feature.GOOGLE_EXPERIENCE",
        ),

        Features("Pixel 2016",
            "com.google.android.apps.photos.NEXUS_PRELOAD",
            "com.google.android.apps.photos.nexus_preload",
            "com.google.android.apps.photos.PIXEL_PRELOAD",
            "com.google.android.apps.photos.PIXEL_2016_PRELOAD",
        ),

        Features("Pixel 2017",
            "com.google.android.feature.PIXEL_2017_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2017_PRELOAD",
        ),

        Features("Pixel 2018",
            "com.google.android.feature.PIXEL_2018_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2018_PRELOAD",
        ),

        Features("Pixel 2019",
            "com.google.android.feature.PIXEL_2019_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2019_PRELOAD",
        ),

        Features("Pixel 2019 mid-year",
            "com.google.android.feature.PIXEL_2019_MIDYEAR_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2019_MIDYEAR_PRELOAD",
        ),

        Features("Pixel 2020",
            "com.google.android.feature.PIXEL_2020_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2020_PRELOAD",
        ),

        Features("Pixel 2020 mid-year",
            "com.google.android.feature.PIXEL_2020_MIDYEAR_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2020_MIDYEAR_PRELOAD",
        ),

        Features("Pixel 2021",
            "com.google.android.feature.PIXEL_2021_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2021_PRELOAD",
        ),

        Features("Pixel 2021 mid-year",
            "com.google.android.feature.PIXEL_2021_MIDYEAR_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2021_MIDYEAR_PRELOAD",
        ),

        Features("Pixel 2022",
            "com.google.android.feature.PIXEL_2022_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2022_PRELOAD",
        ),

        Features("Pixel 2022 mid-year",
            "com.google.android.feature.PIXEL_2022_MIDYEAR_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2022_MIDYEAR_PRELOAD",
        ),

        Features("Pixel 2023",
            "com.google.android.feature.PIXEL_2023_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2023_PRELOAD",
        ),

        Features("Pixel 2023 mid-year",
            "com.google.android.feature.PIXEL_2023_MIDYEAR_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2023_MIDYEAR_PRELOAD",
        ),

        Features("Pixel 2024",
            "com.google.android.feature.PIXEL_2024_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2024_PRELOAD",
        ),

        Features("Pixel 2024 mid-year",
            "com.google.android.feature.PIXEL_2024_MIDYEAR_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2024_MIDYEAR_PRELOAD",
        ),
    )

    /**
     * Example if [featureLevel] = "Pixel 2020", return will have
     * list of all elements from [allFeatures] from "Pixel 2016" i.e. index = 0,
     * to "Pixel 2020" i.e index = 6, both inclusive.
     */
    private fun getFeaturesUpTo(featureLevel: String): List<Features> {
        val allFeatureDisplayNames = allFeatures.map { it.displayName }
        val levelIndex = allFeatureDisplayNames.indexOf(featureLevel)
        return if (levelIndex == -1) listOf()
        else {
            allFeatures.withIndex().filter { it.index <= levelIndex }.map { it.value }
        }
    }

    /**
     * Class storing android version information to be faked.
     *
     * @param label Just a string to show the user. Not spoofed.
     * @param release Corresponds to `ro.build.version.release`. Example values: "12", "11", "8.1.0" etc.
     * @param sdk Corresponds to `ro.build.version.sdk`.
     */
    data class AndroidVersion(
        val label: String,
        val release: String,
        val sdk: Int,
    ){
        fun getAsMap() = hashMapOf(
            Pair("RELEASE", release),
            Pair("SDK_INT", sdk),
            Pair("SDK", sdk.toString()),
        )
    }

    /**
     * List of all major android versions.
     * Pixel 1 series launched with nougat, so that is the lowest version.
     */
    val allAndroidVersions = listOf(
        AndroidVersion("Nougat 7.1.2", "7.1.2", 25),
        AndroidVersion("Oreo 8.1.0", "8.1.0", 27),
        AndroidVersion("Pie 9.0", "9", 28),
        AndroidVersion("Q 10.0", "10", 29),
        AndroidVersion("R 11.0", "11", 30),
        AndroidVersion("S 12.0", "12", 31),
        AndroidVersion("T 13.0", "13", 33),
        AndroidVersion("U 14.0", "14", 34),
    )

    /**
     * Get instance of [AndroidVersion] from specified [label].
     * Send null if no such label.
     */
    fun getAndroidVersionFromLabel(label: String) = allAndroidVersions.find { it.label == label }

    /**
     * Class to contain device names and their respective build properties.
     * @param deviceName Actual device names, example "Pixel 4a".
     * @param props Contains the device properties to spoof.
     * @param featureLevelName Points to the features expected to be spoofed from [allFeatures],
     * from "Pixel 2016" up to this level.
     */
    data class DeviceEntries(
        val deviceName: String,
        val props: HashMap<String, String>,
        val featureLevelName: String,
        val androidVersion: AndroidVersion?,
    )

    /**
     * List of all devices and their build props and their required feature levels.
     */
    val allDevices = listOf(

        DeviceEntries("None", hashMapOf(), "None", null),

        DeviceEntries(
            "Pixel XL", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "marlin"),
                Pair("PRODUCT", "marlin"),
                Pair("HARDWARE", "marlin"),
                Pair("MODEL", "Pixel XL"),
                Pair("ID", "QP1A.191005.007.A3"),
                Pair("FINGERPRINT", "google/marlin/marlin:10/QP1A.191005.007.A3/5972272:user/release-keys"),
            ),
            "Pixel 2016",
            getAndroidVersionFromLabel("Q 10.0"),
        ),

        DeviceEntries(
            "Pixel 2", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "walleye"),
                Pair("PRODUCT", "walleye"),
                Pair("HARDWARE", "walleye"),
                Pair("MODEL", "Pixel 2"),
                Pair("ID", "OPM1.171019.011"),
                Pair("FINGERPRINT", "google/walleye/walleye:8.1.0/OPM1.171019.011/4448085:user/release-keys"),
            ),
            "Pixel 2017",
            getAndroidVersionFromLabel("Oreo 8.1.0"),
        ),

        DeviceEntries(
            "Pixel 3 XL", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "crosshatch"),
                Pair("PRODUCT", "crosshatch"),
                Pair("HARDWARE", "crosshatch"),
                Pair("MODEL", "Pixel 3 XL"),
                Pair("ID", "RQ3A.211001.001"),
                Pair("FINGERPRINT", "google/crosshatch/crosshatch:11/RQ3A.211001.001/7641976:user/release-keys"),
            ),
            "Pixel 2018",
            getAndroidVersionFromLabel("R 11.0"),
        ),

        DeviceEntries(
            "Pixel 3a XL", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "bonito"),
                Pair("PRODUCT", "bonito"),
                Pair("HARDWARE", "bonito"),
                Pair("MODEL", "Pixel 3a XL"),
                Pair("ID", "RQ3A.211001.001"),
                Pair("FINGERPRINT", "google/bonito/bonito:11/RQ3A.211001.001/7641976:user/release-keys"),
            ),
            "Pixel 2019 mid-year",
            getAndroidVersionFromLabel("R 11.0"),
        ),

        DeviceEntries(
            "Pixel 4 XL", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "coral"),
                Pair("PRODUCT", "coral"),
                Pair("HARDWARE", "coral"),
                Pair("MODEL", "Pixel 4 XL"),
                Pair("ID", "SP1A.211105.002"),
                Pair("FINGERPRINT", "google/coral/coral:12/SP1A.211105.002/7743617:user/release-keys"),
            ),
            "Pixel 2019 mid-year",
            getAndroidVersionFromLabel("S 12.0"),
        ),

        DeviceEntries(
            "Pixel 4a", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "sunfish"),
                Pair("PRODUCT", "sunfish"),
                Pair("HARDWARE", "sunfish"),
                Pair("MODEL", "Pixel 4a"),
                Pair("ID", "RQ3A.211001.001"),
                Pair("FINGERPRINT", "google/sunfish/sunfish:11/RQ3A.211001.001/7641976:user/release-keys"),
            ),
            "Pixel 2020 mid-year",
            getAndroidVersionFromLabel("R 11.0"),
        ),

        DeviceEntries(
            "Pixel 5", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "redfin"),
                Pair("PRODUCT", "redfin"),
                Pair("HARDWARE", "redfin"),
                Pair("MODEL", "Pixel 5"),
                Pair("ID", "TQ3A.230901.001"),
                Pair("FINGERPRINT", "google/redfin/redfin:13/TQ3A.230901.001/10750268:user/release-keys"),
            ),
            "Pixel 2020 mid-year",
            getAndroidVersionFromLabel("T 13.0"),
        ),

        DeviceEntries(
            "Pixel 5a", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "barbet"),
                Pair("PRODUCT", "barbet"),
                Pair("HARDWARE", "barbet"),
                Pair("MODEL", "Pixel 5a"),
                Pair("ID", "RD2A.211001.002"),
                Pair("FINGERPRINT", "google/barbet/barbet:11/RD2A.211001.002/7644766:user/release-keys"),
            ),
            "Pixel 2021 mid-year",
            getAndroidVersionFromLabel("R 11.0"),
        ),

        DeviceEntries(
            "Pixel 6 Pro", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "raven"),
                Pair("PRODUCT", "raven"),
                Pair("HARDWARE", "raven"),
                Pair("MODEL", "Pixel 6 Pro"),
                Pair("ID", "SD1A.210817.036"),
                Pair("FINGERPRINT", "google/raven/raven:12/SD1A.210817.036/7805805:user/release-keys"),
            ),
            "Pixel 2021 mid-year",
            getAndroidVersionFromLabel("S 12.0"),
        ),

        DeviceEntries(
            "Pixel 7 Pro", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "cheetah"),
                Pair("PRODUCT", "cheetah"),
                Pair("HARDWARE", "cheetah"),
                Pair("MODEL", "Pixel 7 Pro"),
                Pair("ID", "TQ3A.230901.001"),
                Pair("FINGERPRINT", "google/cheetah/cheetah:13/TQ3A.230901.001/10750268:user/release-keys"),
            ),
            "Pixel 2022 mid-year",
            getAndroidVersionFromLabel("T 13.0"),
        ),

        DeviceEntries(
            "Pixel 8", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "shiba"),
                Pair("PRODUCT", "shiba"),
                Pair("HARDWARE", "shiba"),
                Pair("BOARD", "shiba"),
                Pair("MODEL", "Pixel 8"),
                Pair("ID", "UD1A.230803.041"),
                Pair("DISPLAY", "shiba-user 14 UD1A.230803.041 10808477 release-keys"),
                Pair("FINGERPRINT", "google/shiba/shiba:14/UD1A.230803.041/10808477:user/release-keys"),
                Pair("BOOTLOADER", "ripcurrent-14.0-10807316"),
                Pair("SOC_MANUFACTURER", "Google"),
                Pair("SOC_MODEL", "Tensor G3"),
                Pair("TYPE", "user"),
                Pair("TAGS", "release-keys"),
            ),
            "Pixel 2023 mid-year",
            getAndroidVersionFromLabel("U 14.0"),
        ),

        DeviceEntries(
            "Pixel 8 Pro", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "husky"),
                Pair("PRODUCT", "husky"),
                Pair("HARDWARE", "husky"),
                Pair("BOARD", "husky"),
                Pair("MODEL", "Pixel 8 Pro"),
                Pair("ID", "UD1A.230803.041"),
                Pair("DISPLAY", "husky-user 14 UD1A.230803.041 10808477 release-keys"),
                Pair("FINGERPRINT", "google/husky/husky:14/UD1A.230803.041/10808477:user/release-keys"),
                Pair("BOOTLOADER", "ripcurrent-14.0-10807316"),
                Pair("SOC_MANUFACTURER", "Google"),
                Pair("SOC_MODEL", "Tensor G3"),
                Pair("TYPE", "user"),
                Pair("TAGS", "release-keys"),
            ),
            "Pixel 2023 mid-year",
            getAndroidVersionFromLabel("U 14.0"),
        ),
    )

    /**
     * Get instance of [DeviceEntries] from a supplied [deviceName].
     */
    fun getDeviceProps(deviceName: String?) = allDevices.find { it.deviceName == deviceName }

    /**
     * Call [getFeaturesUpTo] using a device name rather than feature level.
     * Used in spinner in main activity.
     */
    fun getFeaturesUpToFromDeviceName(deviceName: String?): Set<String>{
        return getDeviceProps(deviceName)?.let {
            getFeaturesUpTo(it.featureLevelName).map { it.displayName }.toSet()
        }?: setOf()
    }

    /**
     * Default name of device to spoof.
     */
    val defaultDeviceName = "Pixel XL"

    /**
     * Default feature level to spoof up to. Corresponds to what is expected for the device in [defaultDeviceName].
     */
    val defaultFeatures = getFeaturesUpTo("Pixel 2016")

}
