import { signalStore, withState } from '@ngrx/signals';

type GlobalState = {
  lightTheme: boolean;
  navigation: 'dashboard' | 'tickets' | 'members' | 'notifications' | 'settings';
};

const initialState: GlobalState = {
  lightTheme: true,
  navigation: 'dashboard',
};

export const GlobalStore = signalStore(withState(initialState));
