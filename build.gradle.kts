buildscript {
    dependencies {
        classpath ("com.google.gms:google-services:4.3.15")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.20")
    }
}
plugins {
    id ("com.android.application") version ("8.0.2") apply false
    id ("com.android.library") version ("8.0.2") apply false
    id ("org.jetbrains.kotlin.android") version ("1.8.20") apply false
    id ("androidx.navigation.safeargs") version ("2.5.0") apply false
}

task clean(type: Delete) {
    delete rootProject.buildDir
}