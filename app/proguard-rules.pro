# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/tasomaniac/android-sdks/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontobfuscate

-keep interface com.tasomaniac.muzei.earthview.data.api.EarthViewApi.** { *; }
-keep class com.tasomaniac.muzei.earthview.data.api.EarthView.** { *; }

#-keep class com.squareup.moshi.** { *; }
#-keep interface com.squareup.moshi.** { *; }
#-dontwarn com.squareup.moshi.**

# Retrofit
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions

-dontwarn okio.**
-dontwarn okhttp3.**

-keepattributes EnclosingMethod
