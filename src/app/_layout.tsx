import { Stack } from 'expo-router';
import { StatusBar } from 'expo-status-bar';
import { PaperProvider } from 'react-native-paper';
import { SafeAreaProvider } from 'react-native-safe-area-context';

import { vectorTheme } from '@/theme';

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
