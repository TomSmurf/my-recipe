import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'myr-error',
  templateUrl: './error.component.html',
})
export class ErrorComponent implements OnInit {
  errorMessage?: string;
  errorKey?: string;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.data.subscribe(routeData => {
      if (routeData['errorMessage']) {
        this.errorKey = routeData['errorMessage'];
      }
    });
  }
}
