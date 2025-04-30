import React, { useEffect, useState } from 'react';
import { StyleSheet, View, Text, Button, Switch, TextInput } from 'react-native';
import uuid from 'react-native-uuid';
import MainPage from './Main';
import Main from './Main';

enum Screen {
  SetupScreen,
  MainScreen
}

export default function App() {
  const [currentScreen, setCurrentScreen] = useState<Screen>(Screen.SetupScreen);
  const [userId, setUserId] = useState<string>("");
  const [isManual, setIsManual] = useState<boolean>(false);

  const start = () => {
    setCurrentScreen(Screen.MainScreen);
  };

  const toggleSwitch = () => setIsManual(previousState => !previousState);

  const randomUserId = () => {
    const newId = uuid.v4();
    setUserId(newId);
  };

  const styles = StyleSheet.create({
    container: {
      flex: 1,
      paddingTop: 20,
      paddingHorizontal: 20,
      backgroundColor: '#FFFF',
    },
    title: {
      fontSize: 24,
      fontWeight: 'bold',
      marginBottom: 20,
      textAlign: 'center',
    },
    rowContainer: {
      fontSize: 14,
      alignItems: 'center',
      marginBottom: 2,
    },
    view: {
      marginTop: 10,
    },
  });

  const SetupScreen = () => (
    <View style={styles.container}>
    <Text style={styles.title}>Setup</Text>
  
    <View style={styles.view}>
      <Text>
        By default Manual Start is toggled off, this allows the justtrack SDK to call sdk.start() automatically right after SDK initialize. If you wish to toggle this on please call sdk.start() manually as well.
      </Text>
    </View>
  
    {/* Switch and Label in Row */}
    <View style={{ flexDirection: 'row', alignItems: 'center', marginVertical: 10 }}>
      <Switch
        trackColor={{ false: '#767577', true: '#81b0ff' }}
        thumbColor={isManual ? '#f5dd4b' : '#f4f3f4'}
        ios_backgroundColor="#3e3e3e"
        onValueChange={toggleSwitch}
        value={isManual}
      />
      <Text style={{ marginLeft: 10 }}>Manual Start</Text>
    </View>
  
    <View style={styles.view}>
      <Text>
        If you already have a mechanism to assign a unique ID to each user, you can share this information with the justtrack SDK. This then allows the backend of the justtrack SDK to associate events received from third parties via that user ID with the correct user on justtrack side      </Text>
    </View>
    <View style={{ flexDirection: 'row', alignItems: 'center', marginVertical: 10 }}>
      <TextInput
        style={{
          flex: 1,
          borderWidth: 1,
          borderColor: '#ccc',
          borderRadius: 5,
          paddingHorizontal: 10,
          height: 40,
        }}
        placeholder="User ID"
        value={userId}
        onChangeText={setUserId}
      />
      <View style={{ marginLeft: 10 }}>
        <Button title="Random" onPress={() => randomUserId()} />
      </View>
    </View>

    <View style={{ marginTop: 20 }}>
      <Button title="Start" onPress={() => start()} />
    </View>
  </View>
  );

  switch (currentScreen) {
    case Screen.SetupScreen:
      return <SetupScreen />;
    case Screen.MainScreen:
      return <Main pIsManual={isManual} pUserId={userId}/>;
    default:
      return <SetupScreen />;
  }
}
