package io.justtrack.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.justtrack.AdImpression;
import io.justtrack.AdUnit;
import io.justtrack.AdvertiserIdInfo;
import io.justtrack.AppEvent;
import io.justtrack.Attribution;
import io.justtrack.Callback;
import io.justtrack.JtLoginEvent;
import io.justtrack.JustTrackSdk;
import io.justtrack.JustTrackSdkBuilder;
import io.justtrack.Money;

public class MainActivity extends Activity {
    private static final String TAG = "justtrack demo";

    // this token is generated in your application dashboard
    public static final String TOKEN = "";
    private JustTrackSdk sdk;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        JustTrackSdkBuilder builder = new JustTrackSdkBuilder(this, TOKEN);

        builder.setManualStart(intent.getBooleanExtra("isManualStart", false));

        if (intent.hasExtra("userId")) {
            String userIdExtra = intent.getStringExtra("userId");

            if (userIdExtra != null) {
                builder.setUserId(userIdExtra);
            }
        }

        // initialize the justtrack SDK
        sdk = builder.build();
    }

    /**
     * The SDK starts tracking the user only after the start method is called.
     * User's unique IDs, install and app events will only be tracked after the method is used.
     * Any event preceding the start call will not be reported by the SDK.
     */
    public void start(View view) {
        sdk.start();
    }

    /**
     * The SDK stops tracking the user entirely.
     * No ID's or app events will be tracked or reported.
     * Any app events collected before the stop method is called will still be reported.
     */
    public void stop(View view) {
        sdk.stop();
    }

    /**
     * We offer a range of predefined events, which you can find listed at io.justtrack.UserEvent.
     * For detailed descriptions of each event, please refer to our documentation at
     * https://docs.justtrack.io/sdk/predefined-events.
     */
    public void sendPredefinedEvent(View view) {
        sdk.publishEvent(
                new JtLoginEvent("success", "facebook")
                        // You can add up to 10 dimensions per event. The constructor already set the "jt_element_name"
                        // dimension, so you have 9 dimensions you can specify left.
                        .addDimension("id", "1")
                        .addDimension("dim", "value")
        ).registerCallback(new Callback<Void>() {
            @Override
            public void resolve(Void unused) {
                displayToast("predefined event sent");
            }

            @Override
            public void reject(@NonNull Throwable throwable) {
                log(throwable);
            }
        });
    }

    /**
     * Here is how you can send your custom event.
     */
    public void sendCustomEvent(View view) {
        sdk.publishEvent(new AppEvent("screen_view_event")
                // You can add up to 10 dimensions to one event.
                .addDimension("stage", "1")
                .addDimension("character", "fighter")
                .addDimension("dim", "value")
        ).registerCallback(new Callback<Void>() {
            @Override
            public void resolve(Void unused) {
                displayToast("custom event sent");
            }

            @Override
            public void reject(@NonNull Throwable throwable) {
                log(throwable);
            }
        });;
    }

    public void forwardAdImpression(View view) {
        sdk.forwardAdImpression(new AdImpression(AdUnit.Banner, "adSdkName")
                .setNetwork("network")
                .setPlacement("placement")
                .setTestGroup("testGroup")
                .setSegmentName("segmentName")
                .setInstanceName("instanceName")
                .setBundleId("bundle.id")
                .setRevenue(new Money(10.0, "USD"))
        );
    }

    public void getAdvertiserId(View view) {
        sdk.getAdvertiserIdInfo().registerCallback(new Callback<AdvertiserIdInfo>() {
            @Override
            public void resolve(AdvertiserIdInfo advertiserIdInfo) {
                if (advertiserIdInfo != null) {
                    String advertiserId = advertiserIdInfo.getAdvertiserId();
                    boolean isLimitedAdTracking = advertiserIdInfo.isLimitedAdTracking();
                    changeText(findViewById(R.id.advertiserIdTextView), getString(
                            R.string.advertiser_id,
                            "" + advertiserId
                    ));
                    changeText(findViewById(R.id.trackingLimitTextView), getString(
                            R.string.tracking_limit,
                            isLimitedAdTracking ? "true" : "false"
                    ));
                }
            }

            @Override
            public void reject(@NonNull Throwable throwable) {
                log(throwable);
            }
        });
    }

    public void getAttribution(View view) {
        sdk.getAttribution().registerCallback(new Callback<Attribution>() {
            @Override
            public void resolve(Attribution response) {
                String campaignName = response.getCampaign().getName();
                changeText(findViewById(R.id.campaignTextView), getString(
                        R.string.campaign_name,
                        campaignName
                ));

            }

            @Override
            public void reject(@NonNull Throwable throwable) {
                log(throwable);
            }
        });
    }

    public void getInstallId(View view) {
        sdk.getInstallInstanceId().registerCallback(new Callback<String>() {
            @Override
            public void resolve(String s) {
                changeText(findViewById(R.id.InstallIdTextView), getString(
                        R.string.install_id_value,
                        s
                ));

            }

            @Override
            public void reject(@NonNull Throwable throwable) {
                log(throwable);
            }
        });
    }


    public void getTestGroupId(View view) {
        sdk.getTestGroupId().registerCallback(new Callback<Integer>() {
            @Override
            public void resolve(Integer testGroupId) {
                if (testGroupId != null) {
                    changeText(
                            findViewById(R.id.testGroupIdTextView),
                            getString(R.string.test_group_id, "" + testGroupId)
                    );
                }
            }

            @Override
            public void reject(@NonNull Throwable throwable) {
                log(throwable);
            }
        });
    }


    @Override
    protected void onDestroy() {
        sdk.shutdown();
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // EITHER:

        // If your app does not make use of the intent returned by `Activity#getIntent()`
        // or you need it to return the latest intent anyway you can
        // just set the intent on the activity and the SDK will take care of the rest
        setIntent(intent);

        // OR:

        // Alternatively you can forward the intent directly to the SDK. In that case you don't need
        // to call `setIntent` on your activity, but have to forward all future intents like this, too:
        sdk.onNewIntent(intent);
    }

    private void log(Throwable throwable) {
        runOnUiThread(() -> Toast.makeText(
                MainActivity.this,
                "reject " + throwable.getMessage(),
                Toast.LENGTH_SHORT
        ).show());
        Log.e(TAG, "Got unexpected error", throwable);
    }

    private void displayToast(String message) {
        runOnUiThread(() -> Toast.makeText(
                MainActivity.this,
                message,
                Toast.LENGTH_SHORT
        ).show());
    }

    private void changeText(TextView textView, String text) {
        runOnUiThread(() -> textView.setText(text));
    }
}
