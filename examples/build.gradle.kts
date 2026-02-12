plugins {
    id("dev.frozenmilk.teamcode") version "11.1.0-1.1.1"
}

ftc {
    kotlin()

    sdk.TeamCode()

    solvers {
        implementation(core(""))
        implementation(pedroPathing(""))
    }

    pedro {
        implementation(ftc)
    }
}

// TODO: migrate once vision is published as part of easy auto libraries
dependencies {
    implementation("org.solverslib:vision")
}