import { NativeModule, requireOptionalNativeModule } from 'expo';

import type {
  NavigationState,
  VectorNavigationModuleEvents,
} from './VectorNavigation.types';

export declare class VectorNavigationModule extends NativeModule<VectorNavigationModuleEvents> {
  getCurrentState(): NavigationState;
  isNotificationAccessEnabled(): boolean;
  openNotificationAccessSettings(): void;
}

export default requireOptionalNativeModule<VectorNavigationModule>(
  'VectorNavigation',
);
