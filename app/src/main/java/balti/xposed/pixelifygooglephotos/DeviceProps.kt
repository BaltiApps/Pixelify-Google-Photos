package balti.xposed.pixelifygooglephotos

/**
 * Build values taken from:
 * Pixel 6:
 * https://github.com/DotOS/android_frameworks_base/blob/dot12/core/java/com/android/internal/util/custom/PixelPropsUtils.java
 * Pixel 2:
 * https://gist.github.com/markstachowski/4069f15c5c989827b9e64c0aec045434
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

    class Features(
        val displayName: String,
        vararg val featureFlags: String,
    )

    val allFeatures = listOf(

        Features("None", ),

        Features("Pixel 2016", // Pixel XL
            "com.google.android.apps.photos.NEXUS_PRELOAD",
            "com.google.android.apps.photos.nexus_preload",
        ),

        Features("Pixel 2017", // Pixel 2
            "com.google.android.feature.PIXEL_2017_EXPERIENCE",
        ),

        Features("Pixel 2018", // Pixel 3 XL
            "com.google.android.feature.PIXEL_2018_EXPERIENCE",
        ),

        Features("Pixel 2019", // Pixel 4 XL
            "com.google.android.feature.PIXEL_2019_EXPERIENCE",
        ),

        Features("Pixel 2019 mid-year", // Pixel 3a XL
            "com.google.android.feature.PIXEL_2019_MIDYEAR_EXPERIENCE",
        ),

        Features("Pixel 2020", // Pixel 5
            "com.google.android.feature.PIXEL_2020_EXPERIENCE",
        ),

        Features("Pixel 2020 mid-year", // Pixel 4a
            "com.google.android.feature.PIXEL_2020_MIDYEAR_EXPERIENCE",
        ),

        Features("Pixel 2021", // Pixel 6 Pro
            "com.google.android.feature.PIXEL_2021_EXPERIENCE",
        ),

        Features("Pixel 2021 mid-year", // Pixel 5a 5G (build props not available yet)
            "com.google.android.feature.PIXEL_2021_MIDYEAR_EXPERIENCE",
        ),
    )

    fun getFeatures(displayName: String) = allFeatures.find { it.displayName == displayName }

    data class DeviceEntries(
        val deviceName: String,
        val props: HashMap<String, String>,
    )

    val defaultDeviceName = "Pixel 5"

    val allDevices = listOf(

        DeviceEntries("None", hashMapOf()),

        DeviceEntries(
            "Pixel XL", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "marlin"),
                Pair("PRODUCT", "marlin"),
                Pair("MODEL", "Pixel XL"),
                Pair("FINGERPRINT", "google/marlin/marlin:10/QP1A.191005.007.A3/5972272:user/release-keys"),
            )
        ),

        DeviceEntries(
            "Pixel 2", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "walleye"),
                Pair("PRODUCT", "walleye"),
                Pair("MODEL", "Pixel 2"),
                Pair("FINGERPRINT", "google/walleye/walleye:8.1.0/OPM1.171019.021/4565141:user/release-keys"),
            )
        ),

        DeviceEntries(
            "Pixel 3 XL", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "crosshatch"),
                Pair("PRODUCT", "crosshatch"),
                Pair("MODEL", "Pixel 3 XL"),
                Pair("FINGERPRINT", "google/crosshatch/crosshatch:11/RQ3A.211001.001/7641976:user/release-keys"),
            )
        ),

        DeviceEntries(
            "Pixel 3a XL", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "bonito"),
                Pair("PRODUCT", "bonito"),
                Pair("MODEL", "Pixel 3a XL"),
                Pair("FINGERPRINT", "google/bonito/bonito:11/RQ3A.211001.001/7641976:user/release-keys"),
            )
        ),

        DeviceEntries(
            "Pixel 4 XL", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "coral"),
                Pair("PRODUCT", "coral"),
                Pair("MODEL", "Pixel 4 XL"),
                Pair("FINGERPRINT", "google/coral/coral:12/SP1A.211105.002/7743617:user/release-keys"),
            )
        ),

        DeviceEntries(
            "Pixel 4a", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "sunfish"),
                Pair("PRODUCT", "sunfish"),
                Pair("MODEL", "Pixel 4a"),
                Pair("FINGERPRINT", "google/sunfish/sunfish:11/RQ3A.211001.001/7641976:user/release-keys"),
            )
        ),

        DeviceEntries(
            "Pixel 5", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "redfin"),
                Pair("PRODUCT", "redfin"),
                Pair("MODEL", "Pixel 5"),
                Pair("FINGERPRINT", "google/redfin/redfin:12/SP1A.211105.003/7757856:user/release-keys"),
            )
        ),

        DeviceEntries(
            "Pixel 6 Pro", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "raven"),
                Pair("PRODUCT", "raven"),
                Pair("MODEL", "Pixel 6 Pro"),
                Pair("FINGERPRINT", "google/raven/raven:12/SD1A.210817.036/7805805:user/release-keys"),
            )
        ),
    )

    fun getDeviceProps(deviceName: String?) = allDevices.find { it.deviceName == deviceName }

}