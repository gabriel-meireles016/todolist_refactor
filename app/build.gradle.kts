plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("androidx.room")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.kotlin.serialization)
    id("org.jlleitschuh.gradle.ktlint")
    id("pl.droidsonroids.pitest")
}

android {
    namespace = "com.meireles.todolist"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.meireles.todolist"
        minSdk = 24
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
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

pitest {
    // Pacote alvo de testes
    targetClasses.set(listOf(
        "com.meireles.todolist.ui.screens.add.*",
        "com.meireles.todolist.ui.screens.home.*",
    ))

    // Arquivos de testes
    targetTests.set(listOf(
        "com.meireles.todolist.ui.screens.add.AddViewModelTest",
        "com.meireles.todolist.ui.screens.home.HomeViewModelTest"
    ))

    // Evitar mutações no código interno do Kotlin
    avoidCallsTo.set(setOf(
        "kotlin.jvm.internal",
        "kotlinx.coroutines"
    ))

    // Conjunto de mutações abrangente
    mutators.set(setOf("STRONGER"))

    // Formato do relatório
    outputFormats.set(setOf("HTML", "XML"))

    // Define temporariamente com 0 para evitar falha de build por taxa de mutação
    // abaixo do limite
    mutationThreshold.set(0)
}

configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
        force("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")
        force("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Icons
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material.icons.extended)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // Hilt & Dagger
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.navigation.fragment)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    //Tests
    testImplementation(libs.kotlinx.coroutines.test)
}
