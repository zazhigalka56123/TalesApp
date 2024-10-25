pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven ("https://sdkpkg.sspnet.tech" )

        // подключение репозитория SDK рекламной сети IronSource
        maven ( "https://android-sdk.is.com" )

        // подключение репозитория SDK рекламной сети Mintegral
        maven (  "https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea" )
        maven ("https://developer.huawei.com/repo/")

    }
}

rootProject.name = "TalesRelease"
include(":app")
 