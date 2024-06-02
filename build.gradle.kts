// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
        classpath("com.github.megatronking:svg-plugin:1.3.9")
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
}
