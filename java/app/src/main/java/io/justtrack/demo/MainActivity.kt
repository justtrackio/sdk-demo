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
import io.justtrack.AdvertiserIdInfo
import io.justtrack.AttributionResponse
import io.justtrack.JustTrackSdk
import io.justtrack.JustTrackSdkBuilder
import io.justtrack.Money
import io.justtrack.Promise
import io.justtrack.UserEvent
import io.justtrack.UserScreenShowEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    fun sendPredefinedEvent(view: View) {
        sdk.publishEvent(
            UserScreenShowEvent("Main", "MainActivity")
                //You can also add additional dimensions in PredefinedEvent as well.
                .setDimension1("paid_user")
                .setDimension2("...")
                .setDimension3("...")
                .build()
        )
    }

    /**
     * Here is how you can send your custom event.
     */
    fun sendCustomEvent(view: View) {
        sdk.publishEvent(UserEvent("screen_view_event").build())
    }

    fun forwardAdImpression(view: View) {
        sdk.forwardAdImpression(
            AdFormat.Banner,
            "adSdkName",
            "adNetwork",
            "placement",
            "abTesting",
            "segmentName",
            "instanceName",
            "bundleId",
            Money(10.0, "USD")
        )
    }

    /**
     * If you already have your own assigned unique ID for the user,
     * you can also send us this custom unique ID and we will link it with the user.
     */
    fun sendCustomUserId(view: View) {
        AlertDialog.Builder(this).apply {
            title = "Send Custom User Id"
            val input = EditText(this@MainActivity)
            setView(input)
            setPositiveButton(
                "Add"
            ) { p0, p1 ->
                val customUserId = input.text.toString()
                sdk.setCustomUserId(customUserId)
            }
        }.create().show()
    }

    fun getAdvertiserId(view: View) {
        sdk.toPromise(sdk.advertiserIdInfo, object : Promise<AdvertiserIdInfo> {
            override fun resolve(advertiserIdInfo: AdvertiserIdInfo?) {
                if (advertiserIdInfo != null) {
                    val advertiserId = advertiserIdInfo.advertiserId
                    val isLimitedAdTracking = advertiserIdInfo.isLimitedAdTracking
                    findViewById<TextView>(R.id.advertiserIdTextView).changeText(
                        getString(
                            R.string.advertiser_id,
                            "" + advertiserId
                        )
                    )
                    findViewById<TextView>(R.id.trackingLimitTextView).changeText(
                        getString(
                            R.string.tracking_limit,
                            if (isLimitedAdTracking) "TRUE" else "FALSE"
                        )
                    )
                }
            }

            override fun reject(throwable: Throwable) {
                log(throwable)
            }
        })
    }

    fun getAttribution(view: View) {
        sdk.toPromise(sdk.attribution, object : Promise<AttributionResponse> {
            override fun resolve(response: AttributionResponse) {
                val userId = response.userId.toString()
                findViewById<TextView>(R.id.userIdTextView).changeText(
                    getString(R.string.user_id, userId)
                )

                val campaignName = response.campaign.name
                findViewById<TextView>(R.id.campaignTextView).changeText(
                    getString(
                        R.string.campaign_name,
                        campaignName
                    )
                )
            }

            override fun reject(throwable: Throwable) {
                log(throwable)
            }
        })

        //Use this if you are using coroutine.
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                try {
                    val responseFuture = sdk.attribution.get()
                    val userId = responseFuture.userId.toString()
                    findViewById<TextView>(R.id.userIdTextView).changeText(
                        getString(
                            R.string.user_id,
                            userId
                        )
                    )

                    val campaignName = responseFuture.campaign.name
                    findViewById<TextView>(R.id.campaignTextView).changeText(
                        getString(
                            R.string.campaign_name,
                            campaignName
                        )
                    )
                } catch (exception: Exception) {
                    log(exception)
                }
            }
        }

    }

    fun getTestGroupId(view: View) {
        sdk.toPromise(sdk.testGroupId, object : Promise<Int> {
            override fun resolve(testGroupId: Int?) {
                if (testGroupId != null) {
                    findViewById<TextView>(R.id.testGroupIdTextView).changeText(
                        getString(
                            R.string.test_group_id,
                            "" + testGroupId.toString()
                        )
                    )

                }
            }

            override fun reject(throwable: Throwable) {
                log(throwable)
            }
        })

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
        runOnUiThread {
            Toast.makeText(
                this@MainActivity,
                "reject ${throwable.message}",
                Toast.LENGTH_SHORT
            ).show()
        }

        Log.e(TAG, "Got unexpected error", throwable)
    }

    fun TextView.changeText(text: String) {
        runOnUiThread {
            setText(text)
        }
    }

    companion object {
        const val TAG = "Demo"

        //this token is generated in your application dashboard
        const val token = "..your token.."
    }
}