# ============================================================
#  KasirKu — ProGuard / R8 Rules
# ============================================================

# Keep all annotations (wajib untuk Room, Compose, dll.)
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Keep semua class package utama app
-keep class com.kasirku.app.** { *; }

# ============================================================
#  ROOM DATABASE
# ============================================================
# Jaga semua Room entity, DAO, dan Database class
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
# Room menggunakan reflection untuk kolom dan constructor
-keepclassmembers class com.kasirku.app.data.model.** {
    public <init>(...);
    <fields>;
}
-keepclassmembers class com.kasirku.app.data.local.** {
    public <init>(...);
    <fields>;
}

# ============================================================
#  KOTLIN COROUTINES
# ============================================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ============================================================
#  KOTLIN REFLECT / SERIALIZATION
# ============================================================
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keep class kotlin.Metadata { *; }

# ============================================================
#  JETPACK COMPOSE
# ============================================================
-keep class androidx.compose.** { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# ============================================================
#  DATASTORE PREFERENCES
# ============================================================
-keep class androidx.datastore.** { *; }
-keep class com.google.protobuf.** { *; }

# ============================================================
#  COIL (Image Loading)
# ============================================================
-keep class coil.** { *; }
-keepclassmembers class coil.** { *; }

# ============================================================
#  ML KIT BARCODE SCANNING
# ============================================================
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.** { *; }
-keepclassmembers class com.google.mlkit.** { *; }
-keepclassmembers class com.google.android.gms.** { *; }

# ============================================================
#  CAMERAX
# ============================================================
-keep class androidx.camera.** { *; }
-keepclassmembers class androidx.camera.** { *; }

# ============================================================
#  BLUETOOTH (untuk thermal printer)
# ============================================================
-keep class android.bluetooth.** { *; }

# ============================================================
#  ACCOMPANIST PERMISSIONS
# ============================================================
-keep class com.google.accompanist.** { *; }

# ============================================================
#  GENERAL ANDROID / ANDROIDX
# ============================================================
-keep class androidx.lifecycle.** { *; }
-keep class androidx.navigation.** { *; }

# Jaga Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Jaga Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ============================================================
#  SUPPRESS WARNINGS
# ============================================================
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn javax.annotation.**
-dontwarn sun.misc.Unsafe

