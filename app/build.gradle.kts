plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.poincares.sdkdemo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.poincares.sdkdemo"
        minSdk = 29
        targetSdk = 34
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
            signingConfig = signingConfigs.getByName("debug")
            isJniDebuggable = false
        }
        debug {
//        getByName("debug") {
            isJniDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

//////////////////For internal development debuging
val poincaresSDKInternalMode = findProperty("poincaresSDKInternalMode") as String? == "true"
println("int app gradl, poincaresSDKInternalMode is set to: $poincaresSDKInternalMode")
if (poincaresSDKInternalMode) {
    dependencies {
        implementation(project(":sdk"))
    }
}
else {
    repositories {
        flatDir {
            dirs("libs")
        }
    }
    dependencies {
        implementation(files("libs/poincaressdk.aar"))
    }
}


