# Pixelify-Google-Photos
LSPosed module to add Google Pixel features on Google Photos for any device.

### Steps to use:
1. Install Magisk, [LSPosed](https://github.com/LSPosed/LSPosed).  
2. Install the apk of this app (available from [Releases](https://github.com/BaltiApps/Pixelify-Google-Photos/releases) page.)  
3. Open LSPosed app and enable the module. Google Photos will be automatically selected.  
4. Reboot. Enjoy. (If needed, you might need to clear data of Google Photos app).  

### How does this module work?
It simply hooks on to `hasSystemFeature()` method under `android.app.ApplicationPackageManager` class. 
Then when Google Photos checks the relevant features which are expected only on Pixel devices, the module passes `true`. 
Thus Google Photos enables Pixel-Exclusive features.  
The features being "spoofed" can be found from:  
[Dot OS sources](https://github.com/DotOS/android_vendor_dot/blob/55f1c26bb6dbb1175d96cf538ae113618caf7d06/prebuilt/common/etc/pixel_2016_exclusive.xml)  
[PixelFeatureDrops magisk module](https://github.com/ayush5harma/PixelFeatureDrops/tree/master/system/etc/sysconfig)  

### Disclaimer!!
The user takes sole responsibility for any damage that might arise due to use of this module.  
This inclused physical damage (to device), injury, data loss, and also legal matters.  
This project was made as a learning initiative and the developer cannot be held liable in any way for the use of it.
