package io.justtrack.demo;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import io.justtrack.JustTrackSdk;
import io.justtrack.JustTrackSdkBuilder;
import io.justtrack.SdkBuilder;
import io.justtrack.UserIdSource;

/** @noinspection ConstantValue*/
public class IronSourceActivity extends Activity {
    public final String ironSourceUserId = "..your user id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ironsource);
        SdkBuilder builder = new JustTrackSdkBuilder(this, MainActivity.TOKEN)
                .setUserId("unique_user_id");
        if (ironSourceUserId != null) {
            builder.setEnableIronSourceIntegration(true, ironSourceUserId);
        } else {
            builder.setEnableIronSourceIntegration(true, UserIdSource.JustTrack);
        }

        builder.build();
    }

    /**
     * If you haven't set up the IronSource SDK using "setEnableIronSourceIntegration,"
     * you can integrate IronSource by using "integrateIronSource" after creating the SDK.
     *
     * @noinspection unused
     */
    private void integrateIronSource(JustTrackSdk sdk) throws ExecutionException, InterruptedException {
        if (ironSourceUserId == null) {
            Future<Void> integrationFuture = sdk.integrateWithIronSource(UserIdSource.JustTrack);
            // if you need to know whether the integration succeeded and is done:
            integrationFuture.get();
        } else {
            sdk.integrateWithIronSource(ironSourceUserId);
        }
        // if no exception is thrown, the integration was successful
    }
}
