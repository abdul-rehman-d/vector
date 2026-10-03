import { useCallback, useEffect, useState } from 'react';
import { AppState, Platform } from 'react-native';

import VectorNavigationModule, {
  type NavigationState,
} from '../../modules/vector-navigation';

const idleState: NavigationState = {
  instruction: null,
  distance: null,
  road: null,
  eta: null,
  remainingDistance: null,
  isNavigating: false,
};

export function useNavigationState() {
  const isAndroid = Platform.OS === 'android';
  const nativeModuleAvailable = isAndroid && VectorNavigationModule !== null;
  const [navigationState, setNavigationState] = useState<NavigationState>(() =>
    nativeModuleAvailable && VectorNavigationModule
      ? VectorNavigationModule.getCurrentState()
      : idleState,
  );
  const [notificationAccessEnabled, setNotificationAccessEnabled] =
    useState(() =>
      nativeModuleAvailable && VectorNavigationModule
        ? VectorNavigationModule.isNotificationAccessEnabled()
        : false,
    );

  const refresh = useCallback(() => {
    if (!nativeModuleAvailable || !VectorNavigationModule) return;

    setNotificationAccessEnabled(
      VectorNavigationModule.isNotificationAccessEnabled(),
    );
    setNavigationState(VectorNavigationModule.getCurrentState());
  }, [nativeModuleAvailable]);

  useEffect(() => {
    if (!nativeModuleAvailable || !VectorNavigationModule) return;

    const navigationSubscription = VectorNavigationModule.addListener(
      'onNavigationStateChanged',
      (state) => {
        if (__DEV__) {
          console.debug('[Vector] Parsed navigation state', state);
        }
        setNavigationState(state);
      },
    );
    const appStateSubscription = AppState.addEventListener('change', (state) => {
      if (state === 'active') refresh();
    });

    return () => {
      navigationSubscription.remove();
      appStateSubscription.remove();
    };
  }, [nativeModuleAvailable, refresh]);

  const openNotificationAccessSettings = useCallback(() => {
    VectorNavigationModule?.openNotificationAccessSettings();
  }, []);

  return {
    navigationState,
    notificationAccessEnabled,
    nativeModuleAvailable,
    openNotificationAccessSettings,
  };
}
