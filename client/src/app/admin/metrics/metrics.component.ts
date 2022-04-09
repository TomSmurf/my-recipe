import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { combineLatest } from 'rxjs';

import { MetricsService } from './metrics.service';
import { Thread } from './metrics.model';

@Component({
  selector: 'jhi-metrics',
  templateUrl: './metrics.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MetricsComponent implements OnInit {
  threads?: Thread[];
  updatingMetrics = true;

  constructor(private metricsService: MetricsService, private changeDetector: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.updatingMetrics = true;
    this.metricsService.threadDump().subscribe(threadDump => {
      this.threads = threadDump.threads;
      this.updatingMetrics = false;
      this.changeDetector.markForCheck();
    });
  }
}
