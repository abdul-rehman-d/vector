import { Platform, StyleSheet, View } from 'react-native';
import { Button, Surface, Text, useTheme } from 'react-native-paper';
import { SafeAreaView } from 'react-native-safe-area-context';

import { NavigationDisplay } from '@/components/NavigationDisplay';
import { useNavigationState } from '@/hooks/useNavigationState';

export default function HomeScreen() {
  const theme = useTheme();
  const {
    navigationState,
    nativeModuleAvailable,
    notificationAccessEnabled,
    openNotificationAccessSettings,
  } = useNavigationState();

  if (Platform.OS === 'ios') {
    return (
      <SafeAreaView style={[styles.safeArea, { backgroundColor: theme.colors.background }]}>
        <View style={styles.centered}>
          <Text variant="displaySmall" style={styles.brand}>
            Vector
          </Text>
          <Text variant="headlineSmall" style={styles.message}>
            Android only for now
          </Text>
          <Text
            variant="bodyLarge"
            style={[styles.explanation, { color: theme.colors.onSurfaceVariant }]}
          >
            iOS does not allow apps to read Google Maps navigation notifications.
          </Text>
        </View>
      </SafeAreaView>
    );
  }

  if (navigationState.isNavigating) {
    return (
      <SafeAreaView style={[styles.safeArea, { backgroundColor: theme.colors.background }]}>
        <NavigationDisplay state={navigationState} />
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={[styles.safeArea, { backgroundColor: theme.colors.background }]}>
      <View style={styles.centered}>
        <Text variant="displaySmall" style={styles.brand}>
          Vector
        </Text>
        <View style={[styles.pulse, { backgroundColor: theme.colors.primary }]} />
        <Text variant="headlineSmall" style={styles.message}>
          Waiting for navigation
        </Text>
        <Text
          variant="bodyLarge"
          style={[styles.explanation, { color: theme.colors.onSurfaceVariant }]}
        >
          Start a route in Google Maps and the next direction will appear here.
        </Text>

        <Surface style={styles.accessCard} elevation={0}>
          <View style={styles.accessRow}>
            <View
              style={[
                styles.statusDot,
                {
                  backgroundColor: notificationAccessEnabled
                    ? theme.colors.primary
                    : theme.colors.error,
                },
              ]}
            />
            <View style={styles.accessCopy}>
              <Text variant="labelLarge">Notification access</Text>
              <Text
                variant="bodyMedium"
                style={{ color: theme.colors.onSurfaceVariant }}
              >
                {notificationAccessEnabled ? 'Enabled' : 'Required'}
              </Text>
            </View>
          </View>
          {!notificationAccessEnabled && nativeModuleAvailable ? (
            <Button
              mode="contained"
              onPress={openNotificationAccessSettings}
              contentStyle={styles.buttonContent}
            >
              Open settings
            </Button>
          ) : null}
        </Surface>

        {!nativeModuleAvailable ? (
          <Text
            variant="bodySmall"
            style={[styles.debugHint, { color: theme.colors.error }]}
          >
            Notification reading requires an Android development build.
          </Text>
        ) : __DEV__ ? (
          <Text
            variant="bodySmall"
            style={[styles.debugHint, { color: theme.colors.onSurfaceVariant }]}
          >
            Raw Google Maps notification fields are logged as VectorNavigation in Logcat.
          </Text>
        ) : null}
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
  },
  centered: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 24,
  },
  brand: {
    fontWeight: '800',
    letterSpacing: -1.5,
  },
  pulse: {
    width: 9,
    height: 9,
    borderRadius: 5,
    marginTop: 24,
    marginBottom: 20,
  },
  message: {
    fontWeight: '600',
    textAlign: 'center',
  },
  explanation: {
    maxWidth: 360,
    marginTop: 12,
    lineHeight: 25,
    textAlign: 'center',
  },
  accessCard: {
    width: '100%',
    maxWidth: 420,
    borderRadius: 18,
    marginTop: 44,
    padding: 18,
  },
  accessRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  statusDot: {
    width: 10,
    height: 10,
    borderRadius: 5,
    marginRight: 14,
  },
  accessCopy: {
    flex: 1,
  },
  buttonContent: {
    minHeight: 46,
  },
  debugHint: {
    maxWidth: 360,
    marginTop: 18,
    textAlign: 'center',
  },
});
