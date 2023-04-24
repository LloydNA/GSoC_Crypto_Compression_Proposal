plugins {
    kotlin("multiplatform") version "1.8.20"
    id("io.github.ttypic.swiftklib") version "0.2.1"
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

swiftklib {
    create("KCryptoSwift") {
        path = file("src/native/crypto-swift")
        packageName("com.objclibs.kcryptoswift")
    }
}

kotlin {
    ios()

    android{
        publishAllLibraryVariants()
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.compilations{
            val main by getting{
                cinterops{
                    create("KCryptoSwift")
                }
            }
        }
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

val iosArmTest: Task by tasks.creating {
    val device = project.findProperty("iosDevice")?.toString() ?: "iPhone 8"
    val testExecutable = kotlin.targets.getByName<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>("iosArm64").binaries.getTest("DEBUG")
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

tasks.getByName("allTests").dependsOn(iosArmTest)
tasks.getByName("allTests").dependsOn(iosTest)
