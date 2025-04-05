plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.chaquo.python") // Keep this line
}

android {
    namespace = "com.example.wakewatch"
    compileSdk = 34
    kotlinOptions {
        jvmTarget = "1.8"
    }

    defaultConfig {
        applicationId = "com.example.wakewatch"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // NDK configuration
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    // Move the Python configuration here - outside defaultConfig
    // This is crucial - it needs to be at this level
    chaquopy {
        defaultConfig {
            // Optional: specify Python version
            version = "3.8"

            // Specify pip packages
            pip {
                install("opencv-python-headless")
                install("numpy")
            }
        }
    }

// Add the dependencies block here, after the android block
    dependencies {
        // Remove catalog references
        // implementation(libs.androidx.core.ktx)
        // implementation(libs.androidx.appcompat)
        // implementation(libs.material)
        // implementation(libs.androidx.constraintlayout)

        // Use direct dependencies instead
        implementation("androidx.core:core-ktx:1.10.1")
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.9.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")

        // Keep your other direct dependencies as they are
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
        implementation("androidx.camera:camera-core:1.2.3")
        implementation("androidx.camera:camera-camera2:1.2.3")
        implementation("androidx.camera:camera-lifecycle:1.2.3")
        implementation("androidx.camera:camera-view:1.2.3")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

        // Replace testing catalog references too
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    }
}