import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MetricsComponent } from './metrics.component';
import { metricsRoute } from './metrics.route';
import { SharedModule } from 'src/app/shared/shared.module';
import { JvmThreadsComponent } from './blocks/jvm-threads/jvm-threads.component';
import { MetricsModalThreadsComponent } from './blocks/metrics-modal-threads/metrics-modal-threads.component';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([metricsRoute])],
  declarations: [
    MetricsComponent,
    JvmThreadsComponent,
    MetricsModalThreadsComponent,
  ],
})
export class MetricsModule {}
