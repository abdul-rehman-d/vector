export type VectorNavigationModuleEvents = {
  onNavigationStateChanged: (state: NavigationState) => void;
};

export type NavigationState = {
  instruction: string | null;
  distance: string | null;
  road: string | null;
  eta: string | null;
  remainingDistance: string | null;
  isNavigating: boolean;
};
