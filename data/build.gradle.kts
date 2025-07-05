plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.bodakesatish.ktor.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

// Add this block for SQLDelight configuration
sqldelight {
    databases {
        create("SchemeDatabase") { // This will be the generated database class name
            packageName.set("com.bodakesatish.ktor.data") // Package for generated database code
            // You can specify source folders if your .sq files are not in the default location
//             srcDirs.setFrom("src/main/db")
        }
    }
}

dependencies {

    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // SQLDelight
    implementation(libs.sqldelight.android.driver) // Android specific driver
    implementation(libs.sqldelight.coroutines.extensions) // For Flow support (optional but recommended)

    // Ktor Client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation) // For JSON parsing
    implementation(libs.ktor.serialization.kotlinx.json) // For Kotlinx Serialization
    implementation(libs.ktor.client.logging)
    
    // Koin for Android
    implementation(libs.koin.android)

    implementation(libs.kotlinx.serialization.json) // Or the latest stable version

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}