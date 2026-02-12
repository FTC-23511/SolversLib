pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		google()
		maven("https://repo.dairy.foundation/releases/")
	}
}

includeBuild("../core") {
	dependencySubstitution {
		substitute(module("org.solverslib:core")).using(project(":"))
	}
}

includeBuild("../pedroPathing") {
	dependencySubstitution {
		substitute(module("org.solverslib:pedroPathing")).using(project(":"))
	}
}

includeBuild("../vision") {
    dependencySubstitution {
        substitute(module("org.solverslib:vision")).using(project(":"))
    }
}
