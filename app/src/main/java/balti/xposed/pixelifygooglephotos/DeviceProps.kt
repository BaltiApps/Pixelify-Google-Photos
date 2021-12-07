package balti.xposed.pixelifygooglephotos

/**
 * Values taken from:
 * Pixel 6:
 * https://github.com/DotOS/android_frameworks_base/blob/dot12/core/java/com/android/internal/util/custom/PixelPropsUtils.java
 * Pixel 2:
 * https://gist.github.com/markstachowski/4069f15c5c989827b9e64c0aec045434
 * All other pixels:
 * https://github.com/orgs/Pixel-Props/repositories
 * Also from
 * https://github.com/DotOS/android_frameworks_base/commit/3f7ea7d070017ed1f38035333f084865865698b2
 */
object DeviceProps {

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