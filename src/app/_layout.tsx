import { Stack } from 'expo-router';
import * as SplashScreen from 'expo-splash-screen';
import { StatusBar } from 'expo-status-bar';
import { PaperProvider } from 'react-native-paper';
import { SafeAreaProvider } from 'react-native-safe-area-context';

import { vectorTheme } from '@/theme';

SplashScreen.setOptions({
  duration: 350,
  fade: true,
});

export default function RootLayout() {
  return (
    <SafeAreaProvider>
      <PaperProvider theme={vectorTheme}>
        <StatusBar style="light" />
        <Stack screenOptions={{ headerShown: false }} />
      </PaperProvider>
    </SafeAreaProvider>
  );
}
