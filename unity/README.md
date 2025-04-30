# justtrack SDK for Unity

This is the [justtrack](https://justtrack.io/) SDK for Unity.

Documentation can be found [here](https://justtrack.gitbook.io/sdk/unity/overview).

This is a Unity demo application that will help you integrate with the justtrack SDK. It provides an
example of how to utilize the different functions of the SDK.

## Install

Check out our latest SDK version [here](https://justtrack.gitbook.io/sdk/unity/overview/changelog).

Add the justtrack registry to the manifest.json file.

```json
{
  "dependencies": {
    "io.justtrack.justtrack-unity-sdk": "5.0.0"
  },
  "scopedRegistries": [
    {
      "name": "JustTrack Registry",
      "url": "https://registry.npmjs.org",
      "scopes": [
        "io.justtrack"
      ]
    }
  ]
}
```
