import { MD3DarkTheme, type MD3Theme } from 'react-native-paper';

export const vectorTheme: MD3Theme = {
  ...MD3DarkTheme,
  roundness: 3,
  colors: {
    ...MD3DarkTheme.colors,
    primary: '#A9F6C5',
    onPrimary: '#082015',
    primaryContainer: '#173D2A',
    onPrimaryContainer: '#C5FFD9',
    background: '#090C0A',
    surface: '#111612',
    surfaceVariant: '#1B231D',
    onSurface: '#F1F6F2',
    onSurfaceVariant: '#AAB6AD',
    outline: '#465149',
    error: '#FFB4AB',
  },
};
