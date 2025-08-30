plugins {
    id("dev.frozenmilk.android-library") version "10.1.1-0.1.3"
    id("dev.frozenmilk.publish") version "0.0.4"
    id("dev.frozenmilk.doc") version "0.0.4"
}

android.namespace = "org.solverslib.pedroPathing"

ftc {
    kotlin

    sdk {
        RobotCore {
            configurationNames += "testImplementation"
        }
        FtcCommon
        Hardware
    }
}

dependencies {
    implementation("org.ejml:ejml-simple:0.39") {
        exclude(group = "org.ejml", module = "ejml-all")
    }
    api("com.pedropathing:ftc:1.1.0-SNAPSHOT")


    afterEvaluate {
        api("org.solverslib:core:$version")
    }
}

repositories {
    maven("https://maven.brott.dev/")
    maven("https://maven.pedropathing.com/")
}

dairyPublishing {
    gitDir = file("..")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "org.solverslib"
            artifactId = "pedroPathing"
            // note that version was previously 2.1.1

            artifact(dairyDoc.dokkaHtmlJar)
            artifact(dairyDoc.dokkaJavadocJar)

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}