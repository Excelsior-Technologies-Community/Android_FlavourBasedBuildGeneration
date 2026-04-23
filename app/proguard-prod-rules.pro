# Prod-specific ProGuard rules (strict)
-keepattributes *Annotation*
-keep class com.ext.flavourbasedbuildtest.model.** { *; }
-keep class com.ext.flavourbasedbuildtest.api.** { *; }
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}
