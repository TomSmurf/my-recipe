import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ThreadDump } from './metrics.model';

@Injectable({ providedIn: 'root' })
export class MetricsService {
  constructor(private http: HttpClient) {}

  threadDump(): Observable<ThreadDump> {
    return this.http.get<ThreadDump>('management/threaddump');
  }

  getMetrics(): Observable<any> {
    return this.http.get<any>(('management/metrics'));
  }
}
