# justtrack SDK for Android

This is the [justtrack](https://justtrack.io/) SDK for Android.

Documentation can be found [here](https://docs.justtrack.io/sdk/5.0.x/android/).

This is a Java demo application that will help you integrate with the justtrack SDK. It provides an
example of how to utilize the different functions of the SDK.

## Install

Check out our latest SDK version [here](https://docs.justtrack.io/sdk/5.0.x/android/overview/changelog).

Install via Gradle:

```groovy
allprojects {
    repositories {
        maven {
            url = uri("https://sdk.justtrack.io/maven")
        }
    }
}
```

And

```groovy
dependencies {
    implementation("io.justtrack:justtrack-android-sdk:5.0.0")
}
```
