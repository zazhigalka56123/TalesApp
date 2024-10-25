plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs")
    id ("kotlin-kapt")
}

android {
    namespace = "ru.tales.forfamily"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.tales.forfamily"
        minSdk = 26
        targetSdk = 34
        versionCode = 7
        versionName = "1.1.0"
        multiDexEnabled =  true // Enable multidex

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("com.google.android.gms:play-services-fido:20.1.0")

//    implementation("androidx.media3:media3-exoplayer:1.2.1")
//    implementation("androidx.media3:media3-exoplayer-dash:1.2.1")
//    implementation("androidx.media3:media3-ui:1.2.1")
//    implementation("androidx.media3:media3-session:1.2.1")
    implementation ("com.android.installreferrer:installreferrer:2.2")

    implementation ("com.appsflyer:af-android-sdk:6.13.0")
    implementation ("com.yandex.android:mobileads:7.5.0")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.lifecycle:lifecycle-process:2.8.6")
    implementation("com.airbnb.android:lottie:6.3.0")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-core:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-dash:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.19.1")
    implementation("com.google.android.exoplayer:extension-mediasession:2.19.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // подключение зависимости Yabbi SDK
    implementation("sspnet.tech:yabbi:1.5.6")

    // пподключение зависимости рекламной сети Applovin SDK
    implementation ("sspnet.tech.adapters:applovin:1.2.3")
    implementation ("com.applovin:applovin-sdk:13.0.0")

    // подключение зависимости рекламной сети IronSource SDK
    implementation ("sspnet.tech.adapters:ironsource:1.3.2")
    implementation ("com.ironsource.sdk:mediationsdk:7.8.1")

    // подключение зависимости рекламной сети Yandex SDK
    implementation ("sspnet.tech.adapters:yandex:1.3.2")
    implementation ("com.huawei.hms:ads-identifier:3.4.62.300")

    //noinspection MobileAdsSdkOutdatedVersion
    // подключение зависимости рекламной сети Mintegral SDK
    implementation ("sspnet.tech.adapters:mintegral:1.3.2")
    implementation ("com.mbridge.msdk.oversea:reward:16.6.31")
    implementation ("com.mbridge.msdk.oversea:newinterstitial:16.6.31")
    implementation("ru.gildor.coroutines:kotlin-coroutines-retrofit:1.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}