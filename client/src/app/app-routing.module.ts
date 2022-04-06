import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { errorRoute } from './layouts/error/error.route';
import { navbarRoute } from './layouts/navbar/navbar.route';

@NgModule({
  imports: [
    RouterModule.forRoot(
      [
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
