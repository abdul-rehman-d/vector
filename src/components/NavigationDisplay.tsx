import { StyleSheet, View } from 'react-native';
import { Text, useTheme } from 'react-native-paper';

import type { NavigationState } from '../../modules/vector-navigation';

type Direction =
  | 'destination'
  | 'left'
  | 'right'
  | 'roundabout'
  | 'slight-left'
  | 'slight-right'
  | 'straight'
  | 'u-turn';

const directionSymbols: Record<Direction, string> = {
  destination: '◆',
  left: '←',
  right: '→',
  roundabout: '↻',
  'slight-left': '↖',
  'slight-right': '↗',
  straight: '↑',
  'u-turn': '↶',
};

function inferDirection(instruction: string | null): Direction {
  const value = instruction?.toLowerCase() ?? '';

  if (/u[- ]?turn/.test(value)) return 'u-turn';
  if (value.includes('roundabout')) return 'roundabout';
  if (/(destination|arrive)/.test(value)) return 'destination';
  if (/(slight|bear) left/.test(value)) return 'slight-left';
  if (/(slight|bear) right/.test(value)) return 'slight-right';
  if (/(left|keep left)/.test(value)) return 'left';
  if (/(right|keep right)/.test(value)) return 'right';
  return 'straight';
}

export function NavigationDisplay({ state }: { state: NavigationState }) {
  const theme = useTheme();
  const symbol = directionSymbols[inferDirection(state.instruction)];

  return (
    <View style={styles.container}>
      <Text
        accessibilityLabel={state.instruction ?? 'Continue straight'}
        style={[styles.symbol, { color: theme.colors.primary }]}
      >
        {symbol}
      </Text>
      <Text variant="headlineLarge" style={styles.instruction}>
        {(state.instruction ?? 'Continue').toUpperCase()}
      </Text>
      {state.distance ? (
        <Text style={[styles.distance, { color: theme.colors.primary }]}>
          {state.distance}
        </Text>
      ) : null}
      {state.road ? (
        <Text variant="headlineSmall" style={styles.road} numberOfLines={2}>
          {state.road}
        </Text>
      ) : null}
      {state.eta || state.remainingDistance ? (
        <Text
          variant="titleMedium"
          style={[styles.summary, { color: theme.colors.onSurfaceVariant }]}
        >
          {[state.eta, state.remainingDistance].filter(Boolean).join('  •  ')}
        </Text>
      ) : null}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 28,
    paddingBottom: 32,
  },
  symbol: {
    fontSize: 104,
    fontWeight: '300',
    lineHeight: 118,
    marginBottom: 12,
  },
  instruction: {
    fontWeight: '800',
    letterSpacing: 1.5,
    textAlign: 'center',
  },
  distance: {
    fontSize: 54,
    fontWeight: '700',
    lineHeight: 66,
    marginTop: 20,
  },
  road: {
    fontWeight: '600',
    marginTop: 22,
    textAlign: 'center',
  },
  summary: {
    marginTop: 18,
    textAlign: 'center',
  },
});
