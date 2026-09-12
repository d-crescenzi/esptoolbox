plugins {
    alias(libs.plugins.android.application)
    // == AGP 9+ compiles Kotlin via built-in support, so the kotlin-android plugin is no longer applied == //
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize")
}

android {
    namespace = "com.crescenzi.esptoolbox"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.crescenzi.esptoolbox"
        minSdk = 27
        targetSdk = 37
        versionCode = 6
        versionName = "2.0.0"

        /* == To avoid resource contamination, supported languages: En, It == */
        androidResources.localeFilters.addAll(listOf("it", "hi", "es", "fr", "de", "zh", "ja", "pt"))

    }

    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            isJniDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.animation.graphics)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.androidx.ui.tooling)

    implementation(project(":esp32"))


    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.io.insert.koin.koin.android) // == Dependency Injection == //
    implementation(libs.koin.androidx.compose)
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)
}
