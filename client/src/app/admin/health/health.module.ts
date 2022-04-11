import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { HealthComponent } from './health.component';
import { HealthModalComponent } from './modal/health-modal.component';
import { healthRoute } from './health.route';
import { SharedModule } from 'src/app/shared/shared.module';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([healthRoute])],
  declarations: [HealthComponent, HealthModalComponent],
  entryComponents: [HealthModalComponent],
})
export class HealthModule {}
