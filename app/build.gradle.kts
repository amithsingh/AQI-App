plugins {
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")

    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.aqidemoapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.aqidemoapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    implementation(libs.androidx.material3)

    implementation (libs.androidx.material)


    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity.composeViewmodel)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.service)
    implementation(libs.coroutines.android)
    implementation(libs.viewModelLifeCycle)
    implementation(libs.viewModel.lifecycle.runtime.ktx)
    implementation(libs.viewModel.lifecycle.viewmodel.ktx)
    implementation(libs.viewModel.lifecycle.livedata.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.activityVersion)
    implementation(libs.fragmentVersion)
    implementation(libs.hilt.dependency)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.junit.ktx)
    kapt(libs.dagger)

    //Navigation

    implementation(libs.android.navigation)
    implementation(libs.android.navigation.hilt)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    implementation ("com.google.accompanist:accompanist-permissions:0.30.1")
// Location Services
    implementation ("com.google.android.gms:play-services-location:21.3.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    testImplementation(libs.android.instantRule)
    androidTestImplementation(libs.android.instantRule)
    androidTestImplementation(libs.assertj.core)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}