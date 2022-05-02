import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'logs',
        loadChildren: () => import('./logs/logs.module').then(m => m.LogsModule),
      },
      {
        path: 'configuration',
        loadChildren: () => import('./configuration/configuration.module').then(m => m.ConfigurationModule),
      },
      {
        path: 'health',
        loadChildren: () => import('./health/health.module').then(m => m.HealthModule),
      },
      {
        path: 'metrics',
        loadChildren: () => import('./metrics/metrics.module').then(m => m.MetricsModule),
      },
      {
        path: 'users',
        loadChildren: () => import('./users/user.module').then(m => m.UserModule),
        data: {
          pageTitle: 'Users',
        },
      },
    ]),
  ],
})
export class AdminRoutingModule { }
