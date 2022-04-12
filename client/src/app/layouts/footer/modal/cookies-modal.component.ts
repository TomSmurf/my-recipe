import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'myr-cookies-modal',
  templateUrl: './cookies-modal.component.html',
})
export class CookiesModalComponent {

  constructor(private activeModal: NgbActiveModal) {}

  dismiss(): void {
    this.activeModal.dismiss();
  }
}
