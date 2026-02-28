plugins {
    id("dev.frozenmilk.teamcode") version "11.1.0-1.1.1"
}

ftc {
    kotlin()

    sdk.TeamCode()

    solvers {
        implementation(core(""))
        implementation(pedroPathing(""))
//        implementation(photon(""))
    }


}

// TODO: migrate once photon is published as part of easy auto libraries
dependencies {
    implementation("org.solverslib:photon")
    implementation("com.pedropathing:ftc:2.0.6")
}