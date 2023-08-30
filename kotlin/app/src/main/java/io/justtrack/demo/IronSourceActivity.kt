package io.justtrack.demo

import android.app.Activity
import android.os.Bundle
import io.justtrack.JustTrackSdk
import io.justtrack.JustTrackSdkBuilder
import io.justtrack.SdkBuilder
import io.justtrack.UserIdSource

/**
 * This activity demonstrates how to initialize our SDK and integrate it with IronSource.
 */
class IronSourceActivity : Activity() {
    @Suppress("RedundantNullableReturnType")
    private val customUserId: String? = "..your user id"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ironsource)

        val builder: SdkBuilder = JustTrackSdkBuilder(this, MainActivity.token)
        if (customUserId != null) {
            // or, if you want to provide your own user id:
            builder.setEnableIronSourceIntegration(true, customUserId)
        } else {
            builder.setEnableIronSourceIntegration(true, UserIdSource.JustTrack)
        }
        builder.build()
    }

    /**
     * If you haven't set up the IronSource SDK using "setEnableIronSourceIntegration,"
     * you can integrate IronSource by using "integrateIronSource" after creating the SDK.
     */
    private suspend fun integrateIronSource(sdk: JustTrackSdk) {
        if (customUserId == null) {
            val integrationFuture = sdk.integrateWithIronSource(UserIdSource.JustTrack)
            // if you need to know whether the integration succeeded and is done:
            integrationFuture.await()
        } else {
            sdk.integrateWithIronSource(customUserId)
        }
        // if no exception is thrown, the integration was successful
    }

}