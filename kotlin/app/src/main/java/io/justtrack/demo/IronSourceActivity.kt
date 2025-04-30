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
    private val ironSourceUserId: String? = "..your iron source user id"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ironsource)

        val builder: SdkBuilder = JustTrackSdkBuilder(this, MainActivity.TOKEN)
            .setUserId("unique_user_id")

        if (ironSourceUserId != null) {
            // or, if you want to provide your own user id:
            builder.setEnableIronSourceIntegration(true, ironSourceUserId)
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
        if (ironSourceUserId == null) {
            val integrationFuture = sdk.integrateWithIronSource(UserIdSource.JustTrack)
            // if you need to know whether the integration succeeded and is done:
            integrationFuture.await()
        } else {
            sdk.integrateWithIronSource(ironSourceUserId)
        }
        // if no exception is thrown, the integration was successful
    }

}