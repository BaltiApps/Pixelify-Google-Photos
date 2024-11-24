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

        Features("Pixel Core",
            "com.google.android.feature.PIXEL_EXPERIENCE",
            "com.google.android.feature.GOOGLE_BUILD",
            "com.google.android.feature.GOOGLE_EXPERIENCE",
        ),

        Features("Pixel 2016 - Pixel XL (original quality)",
            "com.google.android.apps.photos.NEXUS_PRELOAD",
            "com.google.android.apps.photos.nexus_preload",
            "com.google.android.apps.photos.PIXEL_PRELOAD",
            "com.google.android.apps.photos.PIXEL_2016_PRELOAD",
        ),

        Features("Pixel 2017 - Pixel 2",
            "com.google.android.feature.PIXEL_2017_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2017_PRELOAD",
        ),

        Features("Pixel 2018 - Pixel 3",
            "com.google.android.feature.PIXEL_2018_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2018_PRELOAD",
        ),

        Features("Pixel 2019 - Pixel 4",
            "com.google.android.feature.PIXEL_2019_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2019_PRELOAD",
            "com.google.android.feature.PIXEL_2019_MIDYEAR_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2019_MIDYEAR_PRELOAD",
        ),

        Features("Pixel 2020 - Pixel 5 (Storage saver quality)",
            "com.google.android.feature.PIXEL_2020_EXPERIENCE",
            "com.google.android.feature.PIXEL_2020_MIDYEAR_EXPERIENCE",
        ),

        Features("Pixel 2021",
            "com.google.android.feature.PIXEL_2021_EXPERIENCE",
        ),

        Features("Pixel 2021 mid-year",
            "com.google.android.feature.PIXEL_2021_MIDYEAR_EXPERIENCE",
        ),

        Features("Pixel 2022",
            "com.google.android.feature.PIXEL_2022_EXPERIENCE",
        ),

        Features("Pixel 2022 mid-year",
            "com.google.android.feature.PIXEL_2022_MIDYEAR_EXPERIENCE",
        ),

        Features("Pixel 2023",
            "com.google.android.feature.PIXEL_2023_EXPERIENCE",
        ),

        Features("Pixel 2023 mid-year",
            "com.google.android.feature.PIXEL_2023_MIDYEAR_EXPERIENCE",
        ),

        Features("Pixel Tablet 2023",
            "com.google.android.feature.PIXEL_TABLET_2023_EXPERIENCE",
        ),

        Features("Pixel 2024",
            "com.google.android.feature.PIXEL_2024_EXPERIENCE",
        ),

        Features("Pixel 2024 mid-year",
            "com.google.android.feature.PIXEL_2024_MIDYEAR_EXPERIENCE",
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
            "Pixel XL (original)", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "marlin"),
                Pair("PRODUCT", "marlin"),
                Pair("HARDWARE", "marlin"),
                Pair("BOARD", "marlin"),
                Pair("MODEL", "Pixel XL"),
                Pair("ID", "QP1A.191005.007.A3"),
                Pair("DISPLAY", "marlin-user 10 QP1A.191005.007.A3 5972272 release-keys"),
                Pair("FINGERPRINT", "google/marlin/marlin:10/QP1A.191005.007.A3/5972272:user/release-keys"),
                Pair("TYPE", "user"),
                Pair("TAGS", "release-keys"),
            ),
            "Pixel 2016 - Pixel XL (original quality)",
            getAndroidVersionFromLabel("Q 10.0"),
        ),

        DeviceEntries(
            "Pixel 5 (Storage saver)", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "redfin"),
                Pair("PRODUCT", "redfin"),
                Pair("HARDWARE", "redfin"),
                Pair("BOARD", "redfin"),
                Pair("MODEL", "Pixel 5"),
                Pair("ID", "TQ3A.230605.011"),
                Pair("DISPLAY", "redfin-user 13 TQ3A.230605.011 10161073 release-keys"),
                Pair("FINGERPRINT", "google/redfin/redfin:13/TQ3A.230605.011/10161073:user/release-keys"),
                Pair("TYPE", "user"),
                Pair("TAGS", "release-keys"),
            ),
            "Pixel 2020 - Pixel 5 (Storage saver quality)",
            getAndroidVersionFromLabel("T 13.0"),
        ),

        DeviceEntries(
            "Pixel Fold (wip)", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "felix"),
                Pair("PRODUCT", "felix"),
                Pair("HARDWARE", "felix"),
                Pair("BOARD", "felix"),
                Pair("MODEL", "Pixel Fold"),
                Pair("ID", "UP1A.231005.007"),
                Pair("DISPLAY", "felix-user 14 UP1A.231005.007 10754064 release-keys"),
                Pair("FINGERPRINT", "google/felix/felix:14/UP1A.231005.007/10754064:user/release-keys"),
                Pair("TYPE", "user"),
                Pair("TAGS", "release-keys"),
            ),
            "Pixel 2023 mid-year",
            getAndroidVersionFromLabel("U 14.0"),
        ),

        DeviceEntries(
            "Pixel Tablet (wip)", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "tangorpro"),
                Pair("PRODUCT", "tangorpro"),
                Pair("HARDWARE", "tangorpro"),
                Pair("BOARD", "tangorpro"),
                Pair("MODEL", "Pixel Tablet"),
                Pair("ID", "TQ3A.230901.001"),
                Pair("DISPLAY", "tangorpro-user 14 UP1A.231005.007 10754064 release-keys"),
                Pair("FINGERPRINT", "google/tangorpro/tangorpro:14/UP1A.231005.007/10754064:user/release-keys"),
                Pair("TYPE", "user"),
                Pair("TAGS", "release-keys"),
            ),
            "Pixel Tablet 2023",
            getAndroidVersionFromLabel("U 14.0"),
        ),

        DeviceEntries(
            "Pixel 7 Pro (Google 1 VPN)", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "cheetah"),
                Pair("PRODUCT", "cheetah"),
                Pair("HARDWARE", "cheetah"),
                Pair("BOARD", "cheetah"),
                Pair("MODEL", "Pixel 7 Pro"),
                Pair("ID", "TQ2A.230305.008.C1"),
                Pair("DISPLAY", "cheetah-user 13 TQ2A.230305.008.C1 9619669 release-keys"),
                Pair("FINGERPRINT", "google/cheetah/cheetah:13/TQ2A.230305.008.C1/9619669:user/release-keys"),
                Pair("TYPE", "user"),
                Pair("TAGS", "release-keys"),
                Pair("SOC_MANUFACTURER", "Google"),
                Pair("SOC_MODEL", "GS201"),
            ),
            "Pixel 2022 mid-year",
            getAndroidVersionFromLabel("T 13.0"),
        ),

        DeviceEntries(
            "Pixel 8 (wip)", hashMapOf(
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
                Pair("TYPE", "user"),
                Pair("TAGS", "release-keys"),
                Pair("BOOTLOADER", "ripcurrent-14.0-10807316"),
                Pair("SOC_MANUFACTURER", "Google"),
                Pair("SOC_MODEL", "Tensor G3"),
            ),
            "Pixel 2023 mid-year",
            getAndroidVersionFromLabel("U 14.0"),
        ),

        DeviceEntries(
            "Pixel 8 Pro (wip)", hashMapOf(
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
                Pair("TYPE", "user"),
                Pair("TAGS", "release-keys"),
                Pair("BOOTLOADER", "ripcurrent-14.0-10807316"),
                Pair("SOC_MANUFACTURER", "Google"),
                Pair("SOC_MODEL", "Tensor G3"),
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
    val defaultDeviceName = "Pixel XL (original)"

    /**
     * Default feature level to spoof up to. Corresponds to what is expected for the device in [defaultDeviceName].
     */
    val defaultFeatures = getFeaturesUpTo("Pixel 2016 - Pixel XL (original quality)")

}
