import { Routes } from '@angular/router';

import { passwordRoute } from './password/password.route';
import { registerRoute } from './register/register.route';
import { settingsRoute } from './settings/settings.route';

const ACCOUNT_ROUTES = [passwordRoute, registerRoute, settingsRoute];

export const accountState: Routes = [
  {
    path: '',
    children: ACCOUNT_ROUTES,
  },
];
