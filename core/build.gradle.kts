plugins {
    id("dev.frozenmilk.android-library") version "10.3.0-0.1.4"
    id("dev.frozenmilk.publish") version "0.0.4"
    id("dev.frozenmilk.doc") version "0.0.4"
}

android.namespace = "org.solverslib.core"

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
    testImplementation("org.mockito:mockito-core:4.9.0")
}

dairyPublishing {
    gitDir = file("..")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "org.solverslib"
            artifactId = "core"
            // note that version was previously 2.1.1

            artifact(dairyDoc.dokkaHtmlJar)
            artifact(dairyDoc.dokkaJavadocJar)

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}