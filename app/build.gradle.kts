plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "org.pacs.pacs_mobile_application"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.pacs.pacs_mobile_application"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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

    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")

    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.1")

    // Coroutine Lifecycle Scopes
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0")


    compileOnly ("org.projectlombok:lombok:1.18.32")
    annotationProcessor ("org.projectlombok:lombok:1.18.32")

    implementation("androidx.biometric:biometric:1.1.0")

    implementation ("androidx.security:security-crypto:1.1.0-alpha03")

    // Required -- JUnit 4 framework
    testImplementation("junit:junit:4.13.2")
    // Optional -- Robolectric environment
    testImplementation ("androidx.test:core:core:1.5.0")
    // Optional -- Mockito framework
    testImplementation("org.mockito:mockito-core:4.11.0")
    // Optional -- Mockk framework
    testImplementation("io.mockk:mockk:1.9.3")
}