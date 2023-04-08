plugins {
    kotlin("multiplatform") version "1.8.20"
    id("com.android.library")
    id("maven-publish")
    //id("kotlin-android-extensions")
}

group = "me.lloydna"
version = "0.0.1"

repositories {
    google()
    mavenCentral()
}

kotlin {
    ios()

    android{
        publishAllLibraryVariants()
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }


        }

        val iosX64Main by sourceSets.getting
        val iosArm64Main by sourceSets.getting
        val iosX64Test by sourceSets.getting
        val iosArm64Test by sourceSets.getting
        val iosTest by getting{
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
        }
        val iosMain by getting{
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
        }

        tasks.getByName("allTests").dependsOn(iosTest)

        val androidMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
    }
}

android {
    compileSdkVersion(31)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        multiDexEnabled = true
        minSdkVersion(24)
        targetSdkVersion(31)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

val iosTest: Task by tasks.creating {
    val device = project.findProperty("iosDevice")?.toString() ?: "iPhone 8"
    val testExecutable = kotlin.targets.getByName<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>("iosX64").binaries.getTest("DEBUG")
    dependsOn(testExecutable.linkTaskName)
    group = JavaBasePlugin.VERIFICATION_GROUP
    description = "Runs tests for target 'ios' on an iOS simulator"

    doLast {
        exec {
            println(testExecutable.outputFile.absolutePath)
            commandLine( "xcrun", "simctl", "spawn", "--standalone", device, testExecutable.outputFile.absolutePath)
        }
    }
}

tasks.getByName("allTests").dependsOn(iosTest)