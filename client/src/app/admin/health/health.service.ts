import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Health } from './health.model';

@Injectable({ providedIn: 'root' })
export class HealthService {
  constructor(private http: HttpClient) {}

  checkHealth(): Observable<Health> {
    return this.http.get<Health>('management/health');
  }
}
