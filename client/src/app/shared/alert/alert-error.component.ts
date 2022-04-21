import { Component, OnDestroy } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { Alert, AlertService } from 'src/app/core/util/alert.service';
import { EventManager, EventWithContent } from 'src/app/core/util/event-manager.service';

@Component({
  selector: 'myr-alert-error',
  templateUrl: './alert-error.component.html',
})
export class AlertErrorComponent implements OnDestroy {
  alerts: Alert[] = [];
  httpErrorListener: Subscription;

  constructor(private alertService: AlertService, private eventManager: EventManager) {
    this.httpErrorListener = eventManager.subscribe(
      'myRecipe.httpError',
      (response: EventWithContent<unknown> | string) => {
        const httpErrorResponse = (response as EventWithContent<HttpErrorResponse>).content;

        console.log(httpErrorResponse);
        switch (httpErrorResponse.status) {
          case 0:
            this.addErrorAlert('Server not reachable');
            break;
          case 400: {
            if (httpErrorResponse.error !== '' && httpErrorResponse.error.errors) {
              const fieldErrors = httpErrorResponse.error.errors;
              for (const fieldError of fieldErrors) {
                this.addErrorAlert(fieldError);
              }
            } else if (httpErrorResponse.error !== '' && httpErrorResponse.error.message) {
              this.addErrorAlert(httpErrorResponse.error.message);
            } else {
              this.addErrorAlert(httpErrorResponse.statusText);
            }
            break;
          }
          case 404:
            this.addErrorAlert('Not found');
            break;
          default:
            if (httpErrorResponse.error !== '' && httpErrorResponse.error.message) {
              this.addErrorAlert(httpErrorResponse.error.message);
            } else {
              this.addErrorAlert(httpErrorResponse.statusText);
            }
        }
      }
    );
  }

  setClasses(alert: Alert): { [key: string]: boolean } {
    const classes = { 'myr-toast': Boolean(alert.toast) };
    if (alert.position) {
      return { ...classes, [alert.position]: true };
    }
    return classes;
  }

  ngOnDestroy(): void {
    this.eventManager.destroy(this.httpErrorListener);
  }

  close(alert: Alert): void {
    alert.close?.(this.alerts);
  }

  private addErrorAlert(message?: string): void {
    this.alertService.addAlert({ type: 'danger', message }, this.alerts);
  }
}
