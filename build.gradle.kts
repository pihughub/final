// Top-level build file
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    // Remove the id("com.chaquo.python") from here
}

buildscript {
    repositories {
        google()
        mavenCentral()
        // Add this if not already there
        maven { url = uri("https://chaquo.com/maven") }
    }
    dependencies {
        // This is the correct way to add Chaquopy to the classpath
        classpath("com.chaquo.python:gradle:16.0.0")
    }
}