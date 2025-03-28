// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.8.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.21" apply false
    id("convention.detekt")
    id("androidx.navigation.safeargs.kotlin") version "2.8.7" apply false
    id("com.google.devtools.ksp") version libs.versions.ksp.get() apply false
}
