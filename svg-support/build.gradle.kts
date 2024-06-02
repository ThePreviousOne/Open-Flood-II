plugins {
    alias(libs.plugins.android.library)
    id("maven-publish")
}

android {
    compileSdk = 34
    namespace = "com.github.megatronking.svg.support"

    defaultConfig {
        minSdk = 22

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    implementation(libs.androidx.annotations)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}
