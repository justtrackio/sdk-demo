import  { useState, useEffect, ReactNode } from 'react';
import * as JustTrackSdk from 'react-native-justtrack-sdk';
import { JtAdEvent, UserIdSource, AppEvent } from 'react-native-justtrack-sdk';

import {Button,ScrollView,StyleSheet,Text,View} from 'react-native';

type Props = {
    pIsManual: boolean;
    pUserId: string;
};

const Main: React.FC<Props> = ({ pIsManual, pUserId }) => {
  const [mUserId, setUserId] = useState<string>("");
  const [mInstallId, setInstallId] = useState<string>("");
  const [mCampaignId, setCampaignId] = useState<string>("");
  const [mAdvertiserId, setAdvertiserId] = useState<string>("");
  const [mAdvertiserIdLimit, setAdvertiserIdLimit] = useState<string>("");
  const [mTestGroupId, setTestGroupId] = useState<string>("");

  useEffect(() => {
    const initializeSDK = async () => {
        const builder = new JustTrackSdk.JusttrackSdkBuilder('token');
        builder.withManualStart(pIsManual);
        if (pUserId != '') {
            builder.withUserId(pUserId);
        }

        builder.initialize();
    };
    
    initializeSDK();
  }, [pIsManual, pUserId]);

  const start = async () => {
    try {
        await JustTrackSdk.start();
    } catch (error) {
      console.log({ message: 'start', error });
    }
  };

  const stop = async () => {
    try {
        await JustTrackSdk.stop();
    } catch (error) {
      console.log({ message: 'stop', error });
    }
  };

  const getAttribution = async () => {
    try {      
      const attributionData: JustTrackSdk.AttributionData = await JustTrackSdk.getAttribution();
      setUserId(attributionData.justtrackUserId);
      setCampaignId(attributionData.campaign.name);

      setInstallId(await JustTrackSdk.getInstallInstanceId());

      getAdvertiserIdInfo();
      getTestGroupId();
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
        new JustTrackSdk.AdImpression(JustTrackSdk.AdUnit.Banner, 'testSdk')
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
      const customEvent1 = new AppEvent('custom_event_name');
      customEvent1.addDimension('dim_1', 'IT WORKS')
      await JustTrackSdk.publishEvent(customEvent1);
      console.log('Custom event published');
    } catch (error) {
      console.log({ message: 'sendCustomEvent', error });
    }
  };

  const sendPredefinedEvent = async () => {
    try {
      await JustTrackSdk.publishEvent(new JtAdEvent(
        'clicked', 
        'bundle_id', 
        'instance_name',
        'ad_network',
        'ad_placement', 
        'sdk_name', 
        'ad_segment', 
        'ad_unit', 
        'testgroup', 
        3.0, 
        JustTrackSdk.TimeUnitGroup.Seconds
      ));
      console.log('Predefined event published');
    } catch (error) {
      console.log({ message: 'sendPredefinedEvent', error });
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
        <View>
          <Text style={styles.sectionContainer}>User Id: {mUserId}</Text>
          <Text style={styles.sectionContainer}>Install Id: {mInstallId}</Text>
          <Text style={styles.sectionContainer}>Campaign Id: {mCampaignId}</Text>
          <Text style={styles.sectionContainer}>Advertiser Id: {mAdvertiserId}</Text>
          <Text style={styles.sectionContainer}>Tracking Limit: {mAdvertiserIdLimit}</Text>
          <Text style={styles.sectionContainer}>Test Group Id: {mTestGroupId}</Text>

          <View style={styles.sectionContainer}>
            <Button title='Start' onPress= {start}></Button>
          </View>

          <View style={styles.sectionContainer}>
            <Button title='Stop' onPress= {stop}></Button>
          </View>
          <View style={styles.sectionContainer}>
            <Button title='Get Attribution' onPress= {getAttribution}></Button>
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
        </View>
    </ScrollView>
  );
}

export default Main;
