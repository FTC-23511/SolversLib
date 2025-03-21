pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		google()
		maven("https://repo.dairy.foundation/releases/")
	}
}

includeBuild("../SolversLib/core") {
	dependencySubstitution {
		substitute(module("org.solverslib:core")).using(project(":"))
	}
}

includeBuild("examples")
includeBuild("pedroPathing")