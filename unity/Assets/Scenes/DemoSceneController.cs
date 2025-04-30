using JustTrack;
using UnityEngine;
using TMPro;

public class DemoSceneController : MonoBehaviour
{
    [SerializeField]
    private TextMeshProUGUI startStopButtonText;

    private void Start()
    {
        UpdateButtonText();
    }

    public void OnClickStopStart()
    {
        if (JustTrackSDK.IsRunning())
        {
            /*
             * Stop the SDK to prevent tracking the user and sending any user data to the justtrack servers.
             */
            JustTrackSDK.Stop();
        }
        else
        {
            /*
             * Start the SDK to make it operational. You can only call most of the SDK methods after it's started.
             */
            JustTrackSDK.Start();
        }

        UpdateButtonText();
    }

    public void OnClickAnonymize()
    {
        /*
         * By calling the Anonymize() method, you can instruct the justtrack backend to delete identifying information about the user.
         */
        JustTrackSDK.Anonymize(
            () =>
            {
                Log("Anonymize (Success)");
            },
            (message) =>
            {
                Log("Anonymize (Failure) " + message);
            });
    }

    public void OnClickEventWithUnit()
    {
        /*
         * Publish an app event with a Name, Value, and Unit.
         *
         * You can instantiate an AppEvent with a Name, Value, and Unit. The Value can be any number, and the Unit can be either Count, Milliseconds, or Seconds.
         */
        JustTrackSDK.PublishEvent(new AppEvent("value_unit_event", 100, Unit.Count));
    }

    public void OnClickEventWithMoney()
    {
        /*
         * Publish an app event with a Name and a Money value
         *
         * You can instantiate an AppEvent with a Name and a Money value. The value can be any number, and the currency can be any string.
         */
        JustTrackSDK.PublishEvent(new AppEvent("money_event", new Money(100, "EUR")));
    }

    public void OnClickEventWithDimensions()
    {
        /*
         * Publish an app event with dimensions
         * You can add dimensions, which are key-value pairs of strings, to any app event.
         */
        JustTrackSDK.PublishEvent(new AppEvent("event_with_dimensions").AddDimension("dimension_1", "value_1").AddDimension("dimension_2", "value_2"));
    }

    public void OnClickPredefinedEvent()
    {
        /*
         * Publish a predefined event
         * You can also send a predefined event. The SDK provides a list of predefined events, which you can find in the documentation at https://docs.justtrack.io/sdk/5.0.x/overview/predefined-events
         */
        JustTrackSDK.PublishEvent(new JtLoginEvent("login"));
    }

    public void OnClickCustomUserId()
    {
        /*
         * Set a custom user identifier
         *
         * You can set your own user ID to use it instead of the one provided by the SDK.
         */
        JustTrackSDK.SetUserId("custom_user_id");
    }

    public void OnClickTestGroupId()
    {
        /*
         * Get a test group identifier
         * You can get the test group identifier of the current user, which is an integer from the set [1, 2, 3].
         */
        JustTrackSDK.GetTestGroupId(
            (id) =>
            {
                Log("GetTestGroupId (Success) Id: " + id);
            },
            (error) =>
            {
                Log("GetTestGroupId (Failure) Error: " + error);
            });
    }

    public void OnClickAttribution()
    {
        /*
         * Get an attribution
         *
         * You can get attribution data for the current user.
         */
        JustTrackSDKBehaviour.GetAttribution(
            (attribution) =>
            {
                Log("GetAttribution (Success) Attribution: " + attribution);
            },
            (error) =>
            {
                Log("GetAttribution (Failure) Error: " + error);
            });
    }

    public void OnClickAdvertiserInfo()
    {
        /*
         * Get advertiser information
         *
         * You can get the advertiser identifier and the limited ad tracking status for the current user.
         */
        JustTrackSDK.GetAdvertiserIdInfo(
            (advertiserIdInfo) =>
            {
                Log("GetAdvertiserIdInfo (Success) Attribution: " + advertiserIdInfo);
            },
            (error) =>
            {
                Log("GetAdvertiserIdInfo (Failure) Error: " + error);
            });
    }

    public void OnClickAdImpression()
    {
        /*
         * Forward an ad impression
         *
         * You can forward an ad impression to the justtrack backend.
         */
        JustTrackSDK.ForwardAdImpression(
            new AdImpression(AdUnit.Banner, "sdk_name")
                .SetBundleId("bundle_id")
                .SetInstanceName("instance_name")
                .SetNetwork("network")
                .SetPlacement("placement")
                .SetSegmentName("segment_name")
                .SetRevenue(new Money(100, "EUR"))
                .SetTestGroup("test_group")
            );
    }

    private void UpdateButtonText()
    {
        if (startStopButtonText != null)
        {
            startStopButtonText.text = JustTrackSDK.IsRunning() ? "Stop" : "Start";
        }
    }

    private void Log(string message)
    {
        Debug.Log($"[justtrack Demo App] {message}");
    }
}