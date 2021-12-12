# Pixelify-Google-Photos
LSPosed module to add Google Pixel features on Google Photos for any device.  
Also reported to work on EdXposed. Tested by [Jim Wu](https://github.com/MlgmXyysd)  

[LSPosed module repo](https://github.com/Xposed-Modules-Repo/balti.xposed.pixelifygooglephotos.git)  
[Development repo](https://github.com/BaltiApps/Pixelify-Google-Photos.git)  

[Telegram group link](https://t.me/pixelifyGooglePhotos)  

### Steps to use:
1. Install Magisk, [LSPosed](https://github.com/LSPosed/LSPosed) Or [EdXposed](https://github.com/ElderDrivers/EdXposed).  
2. Install the apk of this app (available from [Releases](https://github.com/BaltiApps/Pixelify-Google-Photos/releases) page.)  
3. Open LSPosed / EdXposed app and enable the module. For LSPosed, Google Photos will be automatically selected.  
4. Reboot. Enjoy. (If needed, you might need to clear data of Google Photos app).  

### How does this module work?
It simply hooks on to `hasSystemFeature()` method under `android.app.ApplicationPackageManager` class. 
Then when Google Photos checks the relevant features which are expected only on Pixel devices, the module passes `true`. 
Thus Google Photos enables Pixel-Exclusive features.  
The features being "spoofed" can be found from:  
[Dot OS sources](https://github.com/DotOS/android_vendor_dot/blob/55f1c26bb6dbb1175d96cf538ae113618caf7d06/prebuilt/common/etc/pixel_2016_exclusive.xml)  
[PixelFeatureDrops magisk module](https://github.com/ayush5harma/PixelFeatureDrops/tree/master/system/etc/sysconfig)  
This module can also spoof some of the `build.prop` information like `BRAND`, `MANUFACTURER`, `MODEL`, `FINGERPRINT` of some Pixel devices.  

### Disclaimer!!
The user takes sole responsibility for any damage that might arise due to use of this module.  
This includes physical damage (to device), injury, data loss, and also legal matters.  
This project was made as a learning initiative and the developer cannot be held liable in any way for the use of it.
