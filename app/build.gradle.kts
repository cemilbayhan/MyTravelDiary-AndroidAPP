plugins {
    id 'kotlin-android'
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'kotlin-kapt'
    id("androidx.navigation.safeargs.kotlin")
    id 'com.google.gms.google-services'
}

android {
    namespace 'com.example.mytraveldiary'
    compileSdk 34

    defaultConfig {
        applicationId "com.example.mytraveldiary"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures{
        viewBinding true
    }
    dataBinding {
        enabled = true
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
                targetCompatibility JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = '1.8'
    }
}


def firebaseBomVersion = "32.1.0"
def googleServicesVersion = "4.3.15"
def retrofitVersion = '2.9.0'
def glideVersion = '4.15.1'
dependencies {
    // Google Firebase implementations
    implementation "com.google.gms:google-services:$googleServicesVersion"
    implementation platform("com.google.firebase:firebase-bom:$firebaseBomVersion")
    implementation 'com.google.firebase:firebase-database-ktx'
    implementation 'com.google.firebase:firebase-storage-ktx'
    implementation 'com.google.firebase:firebase-auth-ktx'
    implementation 'com.google.firebase:firebase-analytics-ktx'

    // Retrofit implementations
    implementation "com.squareup.retrofit2:retrofit:$retrofitVersion"
    implementation "com.squareup.retrofit2:converter-gson:$retrofitVersion"


    // Navigation component implementations
    implementation "androidx.navigation:navigation-fragment-ktx:2.6.0"
    implementation "androidx.navigation:navigation-ui-ktx:2.6.0"

    // Android Jetpack library implementations
    implementation "android.arch.lifecycle:extensions:1.1.1"
    implementation "androidx.fragment:fragment-ktx:1.6.0"
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4'

    // Image implementations
    implementation "com.github.bumptech.glide:glide:$glideVersion"
    implementation 'de.hdodenhof:circleimageview:3.1.0'
    implementation 'com.github.yalantis:ucrop:2.2.6-native'
    implementation 'com.squareup.picasso:picasso:2.8'


    implementation 'androidx.core:core-ktx:1.10.1'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}