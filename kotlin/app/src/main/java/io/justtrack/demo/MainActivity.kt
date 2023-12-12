package io.justtrack.demo

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import io.justtrack.AdFormat
import io.justtrack.AdImpression
import io.justtrack.JtScreenShowEvent
import io.justtrack.JustTrackSdk
import io.justtrack.JustTrackSdkBuilder
import io.justtrack.Money
import io.justtrack.UserEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : Activity() {
    private lateinit var sdk: JustTrackSdk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //initialize the justtrack SDK
        sdk = JustTrackSdkBuilder(this, token).build()
    }

    /**
     * We offer a range of predefined events, which you can find listed at io.justtrack.UserEvent.
     * For detailed descriptions of each event, please refer to our documentation at
     * https://docs.justtrack.io/sdk/predefined-events.
     */
    fun sendPredefinedEvent(@Suppress("UNUSED_PARAMETER") view: View) {
        sdk.publishEvent(
            JtScreenShowEvent("Main").apply {
                // You can add up to 10 dimensions per event. The constructor already set the "jt_element_name"
                // dimension, so you have 9 dimensions you can specify left.
                addDimension("stage", "1")
                addDimension("character", "fighter")
                addDimension("dim", "value")
            }
        )
    }

    /**
     * Here is how you can send your custom event.
     */
    fun sendCustomEvent(@Suppress("UNUSED_PARAMETER") view: View) {
        sdk.publishEvent(UserEvent("screen_view_event").apply {
            // You can add up to 10 dimensions to one event.
            addDimension("stage", "1")
            addDimension("character", "fighter")
            addDimension("dim", "value")
        })
    }

    fun forwardAdImpression(@Suppress("UNUSED_PARAMETER") view: View) {
        sdk.forwardAdImpression(
            AdImpression(AdFormat.Banner, "adSdkName").apply {
                setNetwork("network")
                setPlacement("placement")
                setTestGroup("testGroup")
                setSegmentName("segmentName")
                setInstanceName("instanceName")
                setBundleId("bundle.id")
                setRevenue(Money(10.0, "USD"))
            }
        )
    }

    /**
     * If you already have your own assigned unique ID for the user,
     * you can also send us this custom unique ID and we will link it with the user.
     */
    fun sendCustomUserId(@Suppress("UNUSED_PARAMETER") view: View) {
        AlertDialog.Builder(this).apply {
            title = "Send Custom User Id"
            val input = EditText(this@MainActivity)
            setView(input)
            setPositiveButton(
                "Add"
            ) { _, _ ->
                val customUserId = input.text.toString()
                sdk.setCustomUserId(customUserId)
            }
        }.create().show()
    }

    fun getAdvertiserId(@Suppress("UNUSED_PARAMETER") view: View) {
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

    fun getAttribution(@Suppress("UNUSED_PARAMETER") view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = sdk.attribution.await()
                val userId = response.userId.toString()
                findViewById<TextView>(R.id.userIdTextView).text =
                    getString(R.string.user_id, userId)

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

    fun getTestGroupId(@Suppress("UNUSED_PARAMETER") view: View) {
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

    companion object {
        const val TAG = "Demo"

        //this token is generated in your application dashboard
        const val token = "..your token.."
    }
}
