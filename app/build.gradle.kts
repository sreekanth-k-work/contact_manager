plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.myapp.contactsmanager" // Specify your app's namespace here
    compileSdk = 34

    defaultConfig {
        applicationId = "com.myapp.contactsmanager"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material.v180)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx.v272)
    implementation(libs.androidx.monitor)
    // Room components
    implementation (libs.androidx.room.runtime.v250)
    annotationProcessor (libs.androidx.room.compiler.v250) // For Java
    kapt ("androidx.room:room-compiler:2.6.1") // For Kotlin (if using Kotlin)
    // Latest Room version as of this writing


    // Add other dependencies here
}
