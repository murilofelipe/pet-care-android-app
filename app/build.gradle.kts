plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.murilo.petcare"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.murilo.petcare"
        minSdk = 26 // Android 8.0 (Bom para compatibilidade)
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.activity.ktx)
    implementation(libs.androidx.activity.compose)
}