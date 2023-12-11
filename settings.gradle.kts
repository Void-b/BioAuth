pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://dl.bintray.com/shodgson/uareu4500reader")
        }
    }
}

rootProject.name = "BioAuth"
include(":app")
 