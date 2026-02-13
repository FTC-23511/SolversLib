plugins {
    id("dev.frozenmilk.android-library") version "11.1.0-1.1.1"
    id("dev.frozenmilk.publish") version "0.0.5"
    id("dev.frozenmilk.doc") version "0.0.5"
}

android.namespace = "org.solverslib.photon"

dairyPublishing {
    gitDir = file("..")
}

ftc {
    kotlin()

    sdk {
        implementation(RobotCore)
        implementation(FtcCommon)
        implementation(Hardware)

        testImplementation(RobotCore)
    }

    solvers {
        api(core(dairyPublishing.version))
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "org.solverslib"
            artifactId = "photon"

            artifact(dairyDoc.dokkaHtmlJar)
            artifact(dairyDoc.dokkaJavadocJar)

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}