package io.justtrack.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import io.justtrack.AdImpression
import io.justtrack.AdUnit
import io.justtrack.AppEvent
import io.justtrack.Callback
import io.justtrack.JtLoginEvent
import io.justtrack.JustTrackSdk
import io.justtrack.JustTrackSdkBuilder
import io.justtrack.Money
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : Activity() {
    private lateinit var sdk: JustTrackSdk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = intent

        val builder = JustTrackSdkBuilder(this, TOKEN)

        builder.setManualStart(intent.getBooleanExtra("isManualStart", false))

        if (intent.hasExtra("userId")) {
            val userIdExtra = intent.getStringExtra("userId")

            if (userIdExtra != null) {
                builder.setUserId(userIdExtra)
            }
        }

        // initialize the justtrack SDK
        sdk = builder.build()
    }

    /**
     * The SDK starts tracking the user only after the start method is called.
     * User's unique IDs, install and app events will only be tracked after the method is used.
     * Any event preceding the start call will not be reported by the SDK.
     */
    fun start(view: View?) {
        sdk.start()
    }

    /**
     * The SDK stops tracking the user entirely.
     * No ID's or app events will be tracked or reported.
     * Any app events collected before the stop method is called will still be reported.
     */
    fun stop(view: View?) {
        sdk.stop()
    }


    /**
     * We offer a range of predefined events, which you can find listed at io.justtrack.UserEvent.
     * For detailed descriptions of each event, please refer to our documentation at
     * https://docs.justtrack.io/sdk/predefined-events.
     */
    fun sendPredefinedEvent(view: View) {
        sdk.publishEvent(
            JtLoginEvent("success", "facebook") // You can add up to 10 dimensions per event. The constructor already set the "jt_element_name"
                // dimension, so you have 9 dimensions you can specify left.
                .addDimension("id", "1")
                .addDimension("dim", "value")
        ).registerCallback(object : Callback<Void?> {
            override fun resolve(unused: Void?) {
                displayToast("predefined event sent")
            }

            override fun reject(throwable: Throwable) {
                log(throwable)
            }
        })
    }

    /**
     * Here is how you can send your custom event.
     */
    fun sendCustomEvent(view: View) {
        sdk.publishEvent(
            AppEvent("screen_view_event") // You can add up to 10 dimensions to one event.
                .addDimension("stage", "1")
                .addDimension("character", "fighter")
                .addDimension("dim", "value")
        ).registerCallback(object : Callback<Void?> {
            override fun resolve(unused: Void?) {
                displayToast("custom event sent")
            }

            override fun reject(throwable: Throwable) {
                log(throwable)
            }
        })
    }

    fun forwardAdImpression(view: View) {
        sdk.forwardAdImpression(
            AdImpression(AdUnit.Banner, "adSdkName")
                .setNetwork("network")
                .setPlacement("placement")
                .setTestGroup("testGroup")
                .setSegmentName("segmentName")
                .setInstanceName("instanceName")
                .setBundleId("bundle.id")
                .setRevenue(Money(10.0, "USD"))
        )
    }

    fun getAdvertiserId(view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val advertiserIdInfo = sdk.advertiserIdInfo.await()
                val advertiserId = advertiserIdInfo.advertiserId
                val isLimitedAdTracking = advertiserIdInfo.isLimitedAdTracking
                findViewById<TextView>(R.id.advertiserIdTextView).text = getString(
                    R.string.advertiser_id,
                    "" + advertiserId
                )
                findViewById<TextView>(R.id.trackingLimitTextView).text = getString(
                    R.string.tracking_limit,
                    if (isLimitedAdTracking) "TRUE" else "FALSE"
                )
            } catch (throwable: Throwable) {
                log(throwable)
            }
        }
    }

    fun getAttribution(view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = sdk.attribution.await()

                val campaignName = response.campaign.name
                findViewById<TextView>(R.id.campaignTextView).text = getString(
                    R.string.campaign_name,
                    campaignName
                )
            } catch (throwable: Throwable) {
                log(throwable)
            }
        }
    }

    fun getInstallId(view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val installInstanceId = sdk.installInstanceId.await()
                findViewById<TextView>(R.id.InstallIdTextView).text = getString(
                    R.string.install_id_value,
                    installInstanceId
                )
            } catch (throwable: Throwable) {
                log(throwable)
            }
        }
    }

    fun getTestGroupId(view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val testGroupId: Int? = sdk.testGroupId.await()
                findViewById<TextView>(R.id.testGroupIdTextView).text = getString(
                    R.string.test_group_id,
                    "" + testGroupId
                )
            } catch (throwable: Throwable) {
                log(throwable)
            }
        }
    }

    override fun onDestroy() {
        sdk.shutdown()
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // EITHER:

        // If your app does not make use of the intent returned by `Activity#getIntent()`
        // or you need it to return the latest intent anyway you can
        // just set the intent on the activity and the SDK will take care of the rest
        setIntent(intent)

        // OR:

        // Alternatively you can forward the intent directly to the SDK. In that case you don't need
        // to call `setIntent` on your activity, but have to forward all future intents like this, too:
        sdk.onNewIntent(intent)
    }

    private fun log(throwable: Throwable) {
        Toast.makeText(
            this@MainActivity,
            "reject ${throwable.message}",
            Toast.LENGTH_SHORT
        ).show()

        Log.e(TAG, "Got unexpected error", throwable)
    }

    private fun displayToast(message: String) {
        runOnUiThread {
            Toast.makeText(
                this@MainActivity,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        const val TAG = "Demo"

        // this token is generated in your application dashboard
        const val TOKEN = ""
    }
}
