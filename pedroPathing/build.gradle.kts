plugins {
    id("dev.frozenmilk.android-library") version "11.1.0-1.1.1"
    id("dev.frozenmilk.publish") version "0.0.5"
    id("dev.frozenmilk.doc") version "0.0.5"
}

android.namespace = "org.solverslib.pedroPathing"

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

    pedro {
        implementation(ftc)
    }
}

dependencies {
    implementation("org.ejml:ejml-simple:0.39") {
        exclude(group = "org.ejml", module = "ejml-all")
    }
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