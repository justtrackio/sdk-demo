package io.justtrack.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.justtrack.AdFormat;
import io.justtrack.AdImpression;
import io.justtrack.AdvertiserIdInfo;
import io.justtrack.AttributionResponse;
import io.justtrack.JtScreenShowEvent;
import io.justtrack.JustTrackSdk;
import io.justtrack.JustTrackSdkBuilder;
import io.justtrack.Money;
import io.justtrack.Promise;
import io.justtrack.UserEvent;

public class MainActivity extends Activity {
    private static final String TAG = "justtrack demo";

    //this token is generated in your application dashboard
    public static final String token = "..your token..";
    private JustTrackSdk sdk;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize the justtrack SDK
        sdk = new JustTrackSdkBuilder(this, token).build();
    }

    /**
     * We offer a range of predefined events, which you can find listed at io.justtrack.UserEvent.
     * For detailed descriptions of each event, please refer to our documentation at
     * https://docs.justtrack.io/sdk/predefined-events.
     */
    public void sendPredefinedEvent(View view) {
        sdk.publishEvent(
                new JtScreenShowEvent("Main")
                        // You can add up to 10 dimensions per event. The constructor already set the "jt_element_name"
                        // dimension, so you have 9 dimensions you can specify left.
                        .addDimension("stage", "1")
                        .addDimension("character", "fighter")
                        .addDimension("dim", "value")
        );
    }

    /**
     * Here is how you can send your custom event.
     */
    public void sendCustomEvent(View view) {
        sdk.publishEvent(new UserEvent("screen_view_event")
                // You can add up to 10 dimensions to one event.
                .addDimension("stage", "1")
                .addDimension("character", "fighter")
                .addDimension("dim", "value"));
    }

    public void forwardAdImpression(View view) {
        sdk.forwardAdImpression(new AdImpression(AdFormat.Banner, "adSdkName")
                .setNetwork("network")
                .setPlacement("placement")
                .setTestGroup("testGroup")
                .setSegmentName("segmentName")
                .setInstanceName("instanceName")
                .setBundleId("bundle.id")
                .setRevenue(new Money(10.0, "USD"))
        );
    }

    /**
     * If you already have your own assigned unique ID for the user,
     * you can also send us this custom unique ID and we will link it with the user.
     */
    public void sendCustomUserId(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Send Custom User Id");
        EditText input = new EditText(MainActivity.this);
        builder.setView(input);
        builder.setPositiveButton("Add", (dialogInterface, i) -> {
            String customerUserId = input.getText().toString();
            sdk.setCustomUserId(customerUserId);
        });
        builder.create().show();
    }

    public void getAdvertiserId(View view) {
        sdk.getAdvertiserIdInfo().registerPromiseCallback(new Promise<AdvertiserIdInfo>() {
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
        sdk.getAttribution().registerPromiseCallback(new Promise<AttributionResponse>() {
            @Override
            public void resolve(AttributionResponse response) {
                String userId = response.getUserId().toString();
                changeText(findViewById(R.id.userIdTextView), getString(R.string.user_id, userId));

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

    public void getTestGroupId(View view) {
        sdk.getTestGroupId().registerPromiseCallback(new Promise<Integer>() {
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

    private void changeText(TextView textView, String text) {
        runOnUiThread(() -> textView.setText(text));
    }
}
