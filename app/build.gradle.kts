plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android) // ✅ Kotlin Plugin
    alias(libs.plugins.kotlin.kapt) // ✅ Kapt Plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.eventmanagement"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.eventmanagement"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures {
        dataBinding = true // ✅ Enables Data Binding
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    // Glide for image loading
    implementation(libs.glide)
    kapt("com.github.bumptech.glide:compiler:4.16.0") // ✅ Ensure kapt works
}

apply(plugin = "com.google.gms.google-services")