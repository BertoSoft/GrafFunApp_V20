plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.graffunapp_v20"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.graffunapp_v20"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }

}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)


    // Ciclo de vida y ViewModel para Android
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.11.0")
    // Facilita la inicialización de ViewModels en las Activities (por delegación "by viewModels")
    implementation("androidx.activity:activity-ktx:1.13.0")
    // para usar exp4
    implementation("net.objecthunter:exp4j:0.4.8")
}