plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.bodakesatish.ktor"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.bodakesatish.ktor"
        minSdk = 29
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

// Add this block for SQLDelight configuration
sqldelight {
    databases {
        create("AppDatabase") { // This will be the generated database class name
            packageName.set("com.bodakesatish.ktor.cache") // Package for generated database code
            // You can specify source folders if your .sq files are not in the default location
//             srcDirs.setFrom("src/main/db")
        }
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Ktor Client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation) // For JSON parsing
    implementation(libs.ktor.serialization.kotlinx.json) // For Kotlinx Serialization
    implementation(libs.ktor.client.logging)

    // Coroutines for async operations
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    //SqlDelight
//    implementation(libs.sqldelight.android)
//    implementation(libs.sqldelight.coroutines)
//    implementation(libs.sqldelight.runtime)
//    implementation(libs.sqldelight.driver.android)

    // SQLDelight
    implementation("app.cash.sqldelight:android-driver:2.0.1") // Android specific driver
    implementation("app.cash.sqldelight:coroutines-extensions:2.0.1") // For Flow support (optional but recommended)


    // Koin for Android
    implementation(libs.koin.android)
    implementation(libs.androidx.swiperefreshlayout)
    // Optional: Koin for Ktor (can be useful for managing Ktor client if you have complex needs)
    // implementation("io.insert-koin:koin-ktor:$koinVersion")

    // For testing with Koin (example)
    // testImplementation("io.insert-koin:koin-test-junit4:$koinVersion")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}