plugins {
    alias(libs.plugins.android.application)
}

android {
    compileSdk = 34
    namespace = "io.thepreviousone.openfloodii"

    defaultConfig {
        applicationId = "io.thepreviousone.openfloodii"
        minSdk = 22
        targetSdk = 34
        versionCode = 17
        versionName = "2.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.gson)
    implementation(libs.viewpump)
    implementation(libs.eventbus)
    implementation(project(":svg-support"))

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

}