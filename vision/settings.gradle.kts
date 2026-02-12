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
