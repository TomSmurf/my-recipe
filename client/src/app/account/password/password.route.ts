import { Route } from '@angular/router';
import { UserRouteAccessService } from 'src/app/core/auth/user-route-access.service';

import { PasswordComponent } from './password.component';

export const passwordRoute: Route = {
  path: 'password',
  component: PasswordComponent,
  data: {
    pageTitle: 'Password',
  },
  canActivate: [UserRouteAccessService],
};
