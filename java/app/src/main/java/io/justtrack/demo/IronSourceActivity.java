package io.justtrack.demo;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import io.justtrack.IronSourceUserIdSource;
import io.justtrack.JustTrackSdk;
import io.justtrack.JustTrackSdkBuilder;
import io.justtrack.SdkBuilder;

public class IronSourceActivity extends Activity {
    public final String customUserId = "..your user id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ironsource);
        SdkBuilder builder = new JustTrackSdkBuilder(this, MainActivity.token);
        if (customUserId != null) {
            builder.setEnableIronSourceIntegration(true, customUserId);
        } else {
            builder.setEnableIronSourceIntegration(true, IronSourceUserIdSource.JustTrack);
        }

        builder.build();
    }

    /**
     * If you haven't set up the IronSource SDK using "setEnableIronSourceIntegration,"
     * you can integrate IronSource by using "integrateIronSource" after creating the SDK.
     */
    private void integrateIronSource(JustTrackSdk sdk) throws ExecutionException, InterruptedException {
        if (customUserId == null) {
            Future<?> integrationFuture = sdk.integrateWithIronSource(IronSourceUserIdSource.JustTrack);
            // if you need to know whether the integration succeeded and is done:
            integrationFuture.get();
        } else {
            sdk.integrateWithIronSource(customUserId);
        }
        // if no exception is thrown, the integration was successful
    }
}
