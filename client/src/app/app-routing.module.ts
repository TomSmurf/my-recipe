import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Authority } from './config/authority.constants';
import { UserRouteAccessService } from './core/auth/user-route-access.service';
import { errorRoute } from './layouts/error/error.route';
import { navbarRoute } from './layouts/navbar/navbar.route';

@NgModule({
  imports: [
    RouterModule.forRoot(
      [
        {
          path: 'admin',
          data: {
            authorities: [Authority.ADMIN],
          },
          canActivate: [UserRouteAccessService],
          loadChildren: () => import('./admin/admin-routing.module').then(m => m.AdminRoutingModule),
        },
        {
          path: 'account',
          loadChildren: () => import('./account/account.module').then(m => m.AccountModule),
        },
        {
          path: 'login',
          loadChildren: () => import('./login/login.module').then(m => m.LoginModule),
        },
        navbarRoute,
        ...errorRoute,
      ]
    ),
  ],
  exports: [RouterModule],
})
export class AppRoutingModule {}
