# Vector

## Overview

Vector is a small React Native companion app for an ESP32-based navigation display.

The Android app reads active Google Maps navigation notifications, extracts the relevant navigation information, and presents it in a simplified UI matching what will eventually appear on the ESP32 display.

The project will be developed in two clearly separated phases.

## Tech Stack

- React Native
- Expo
- pnpm
- React Native Paper
- TypeScript
- dark mode only
- Android is the primary platform
- iOS should remain buildable and reasonably structured, but Android-specific functionality takes priority
- web support must be removed completely

---

# Phase 1 — Navigation Notification Reader

## Goal

Build the Android app that detects Google Maps navigation notifications and renders the navigation instruction inside Vector.

Phase 1 has **no Bluetooth functionality whatsoever**.

Do not install Bluetooth libraries, create Bluetooth abstractions, add ESP32-related code, or prepare infrastructure for Phase 2.

## Core Flow

Google Maps navigation

→ Android notification

→ Vector NotificationListenerService

→ parse navigation information

→ update React Native state

→ render navigation display

## Android Notification Access

Use Android's native `NotificationListenerService`.

Vector should:

- detect notifications from Google Maps
- react to notification updates while navigation is active
- extract as much useful navigation information as reliably available
- ignore unrelated notifications
- recover cleanly when navigation ends
- provide a way to open Android's Notification Access settings

The Android native implementation can be written in Kotlin.

## Navigation Data Model

Keep the parsed state simple:

```ts
type NavigationState = {
  instruction: string | null;
  distance: string | null;
  road: string | null;
  eta: string | null;
  remainingDistance: string | null;
  isNavigating: boolean;
};
```

Do not over-engineer parsing.

Google Maps notification structure may vary, so parsing should fail gracefully.

## UI

Use React Native Paper.

Dark mode only.

The app should feel like a preview of the eventual ESP32 navigation display rather than a full navigation app.

### Idle State

When no navigation notification exists:

- Vector logo/name
- status such as `Waiting for navigation`
- notification-access status
- button to open Notification Access settings if permission is missing

### Navigation State

When Google Maps navigation is detected, make the navigation information the focus of the screen.

Example:

```text
      ↱

  TURN RIGHT

     300 m

Shahrah-e-Faisal

18 min • 7.2 km
```

Use large, readable text with minimal visual clutter.

The UI should resemble something that could realistically be reproduced on a small ESP32 display.

## Navigation Icons

Support a basic set of direction types where possible:

- left
- right
- slight left
- slight right
- U-turn
- straight
- roundabout
- destination

A simple icon or arrow representation is sufficient.

If the notification cannot reliably determine the maneuver, display the textual instruction instead.

## Debugging

Include a small development/debug view or console logging that exposes the raw Google Maps notification fields so notification parsing can be iterated on easily.

Do not expose this prominently in the normal navigation UI.

## Platform Behaviour

### Android

Full Phase 1 functionality.

### iOS

Keep the project healthy and buildable where reasonably possible.

Since iOS does not provide equivalent general notification-listener access, show an explanatory unsupported state instead of implementing fake functionality.

Do not spend significant Phase 1 effort on iOS.

### Web

Remove web completely.

Do not maintain Expo Web support.

---

# Phase 2 — ESP32 Bluetooth

Phase 2 will be implemented later.

The eventual goal is:

Vector

→ BLE

→ ESP32

→ physical navigation display

Phase 2 will include:

- BLE scanning
- ESP32 discovery
- pairing/connection
- reconnect behaviour
- connection status
- sending navigation state to ESP32
- compact BLE message protocol
- handling disconnected/offline ESP32 devices

None of this should be implemented, scaffolded, or prepared during Phase 1.

---

# Phase 1 Definition of Done

Phase 1 is complete when:

1. project uses Expo + pnpm + TypeScript
2. React Native Paper is configured with a dark theme
3. web support has been removed
4. Android can request/open Notification Access settings
5. Android NotificationListenerService receives notification events
6. Google Maps navigation notifications are identified
7. useful navigation fields are extracted
8. UI updates live as Google Maps navigation changes
9. idle state works correctly when navigation is inactive
10. app remains reasonably buildable on iOS with an unsupported-state screen
11. there is zero Bluetooth or ESP32 integration code
