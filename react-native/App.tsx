import  { useState, useEffect, ReactNode } from 'react';
import * as JustTrackSdk from 'react-native-justtrack-sdk';
import { JtAdClickEvent, UserIdSource, UserEvent } from 'react-native-justtrack-sdk';

import {Button,ScrollView,StyleSheet,Text,View} from 'react-native';


function App(): ReactNode {
  const [sdkInitalized, setSdkInitialized] = useState(false);

  const [mUserId, setUserId] = useState<string>("");
  const [mCampaignId, setCampaignId] = useState<string>("");
  const [mAdvertiserId, setAdvertiserId] = useState<string>("");
  const [mAdvertiserIdLimit, setAdvertiserIdLimit] = useState<string>("");
  const [mTestGroupId, setTestGroupId] = useState<string>("");

  useEffect(() => {
    const initializeSDK = async () => {

      await new JustTrackSdk.JusttrackSdkBuilder('your_token')
      .withInactivityTimeFrameHours(-1)
      .withReAttributionTimeFrameDays(1)
      .withReFetchReAttributionDelaySeconds(-3)
      .withAutomaticInAppPurchaseTracking(true)
      .withCustomUserId("your-custom-user-id")
      .withEnableFirebaseIntegration(false)
      .initialize();
      
      setSdkInitialized(true);
    };

    initializeSDK();
  }, []);

  const getAttribution = async () => {
    try {
      const attributionData: JustTrackSdk.AttributionData = await JustTrackSdk.getAttribution();
      setUserId(attributionData.userId);
      setCampaignId(attributionData.campaign.name);
    } catch (error) {
      console.log({ message: 'getAttribution', error });
    }
  };

  const getAdvertiserIdInfo = async () => {
    try {
      const advertiserInfo: JustTrackSdk.AdvertiserIdInfo = await JustTrackSdk.getAdvertiserIdInfo();
      setAdvertiserId(advertiserInfo.advertiserId !== null ? advertiserInfo.advertiserId : '');
      setAdvertiserIdLimit(advertiserInfo.isLimitedAdTracking ? "true" : "false");
    } catch (error) {
      console.log({ message: 'getAdvertiserIdInfo', error });
    }
  };

  const getTestGroupId = async () => {
    try {
      const testGroupId: JustTrackSdk.TestGroupId | null = await JustTrackSdk.getTestGroupId();
      setTestGroupId(testGroupId !== null ? testGroupId + "" : "");

    } catch (error) {
      console.log({ message: 'getTestGroupId', error });
    }
  };

  const forwardAdImpression = async () => {
    try {
      let adSuccess = await JustTrackSdk.forwardAdImpression(
        new JustTrackSdk.AdImpression(JustTrackSdk.AdFormat.Banner, 'testSdk')
          .withAdNetwork('testNetwork')
          .withPlacement('testPlacement')
          .withTestGroup('testGroup')
          .withSegmentName('testSegment')
          .withInstanceName('testInstance')
          .withRevenue({
            value: 1,
            currency: 'USD',
          })
      );
      console.log('Forwarded banner: ' + adSuccess);
    } catch (error) {
      console.log({ message: 'forwardAdImpression', error });
    }
  };

  const sendCustomEvent = async () => {
    try {
      const customEvent1 = new UserEvent('custom_event_name');
      customEvent1.addDimension('dim_1', 'IT WORKS')
      await JustTrackSdk.publishEvent(customEvent1);
      console.log('Custom event published');
    } catch (error) {
      console.log({ message: 'sendCustomEvent', error });
    }
  };

  const sendPredefinedEvent = async () => {
    try {
      await JustTrackSdk.publishEvent(new JtAdClickEvent('ad_unit', 'sdk_name', 'ad_network', 'ad_placement', 1));
      console.log('Predefined event published');
    } catch (error) {
      console.log({ message: 'sendPredefinedEvent', error });
    }
  };

  const sendCustomUserId = async () => {
    try {
      await JustTrackSdk.setCustomUserId('custom user id');
      console.log('sendCustomUserId completed')
    } catch (error) {
      console.log({ message: 'setCustomUserId', error });
    }
  };

  const integrateWIthIronSource = async () => {
    try {
      await JustTrackSdk.integrateWithIronSource(UserIdSource.JustTrack);
      console.log('integrateWIthIronSource completed')
    } catch (error) {
      console.log({ message: 'integrateWIthIronSource', error });
    }
  };
  const styles = StyleSheet.create({
    backgroundStyle: {
      backgroundColor: 'white'
    },
    sectionContainer: {
      marginTop: 24,
      paddingHorizontal: 16,
    }
  });

  return (
    <ScrollView
      style={styles.backgroundStyle}
      contentInsetAdjustmentBehavior="automatic">
      {sdkInitalized ? (
        <View>
          <Text style={styles.sectionContainer}>User Id: {mUserId}</Text>
          <Text style={styles.sectionContainer}>Campaign Id: {mCampaignId}</Text>
          <Text style={styles.sectionContainer}>Advertiser Id: {mAdvertiserId}</Text>
          <Text style={styles.sectionContainer}>Tracking Limit: {mAdvertiserIdLimit}</Text>
          <Text style={styles.sectionContainer}>Test Group Id: {mTestGroupId}</Text>

          <View style={styles.sectionContainer}>
            <Button title='Get Attribution' onPress= {getAttribution}></Button>
          </View>

          <View style={styles.sectionContainer}>
            <Button title='Get Advertiser Id' onPress= {() => getAdvertiserIdInfo()}></Button>
          </View>

          <View style={styles.sectionContainer}>
            <Button title='Get TestGroup Id' onPress= {() => getTestGroupId()}></Button>
          </View>

          <View style={styles.sectionContainer}>
            <Button title='Send Ads Impression' onPress= {() => forwardAdImpression()}></Button>
          </View>

          <View style={styles.sectionContainer}>
            <Button title='Send Custom Event' onPress= {() => sendCustomEvent()}></Button>
          </View>

          <View style={styles.sectionContainer}>
            <Button title='Send Predefined Event' onPress= {() => sendPredefinedEvent()}></Button>
          </View>

          <View style={styles.sectionContainer}>
            <Button title='Send Custom User Id' onPress= {() => sendCustomUserId()}></Button>
          </View>

          <View style={styles.sectionContainer}>
            <Button title='Integrate with Ironsouce' onPress= {() => integrateWIthIronSource()}></Button>
          </View>
        </View>
      ) : (
        <Text>Loading...</Text>
      )}
    </ScrollView>
  );
}

export default App;
