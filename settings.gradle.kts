pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
}


val poincaresSDKInternalMode = settings.extra["poincaresSDKInternalMode"]?.toString()?.toBoolean() ?: false

rootProject.name = "PoincaresSdkDemo"
include(":app")

println("in setting, poincaresSDKInternalMode is set to: $poincaresSDKInternalMode")
if (poincaresSDKInternalMode) {
//for internal development
    include(":sdk")
    project(":sdk").projectDir = file("../../../makefile/android/sdk")
}