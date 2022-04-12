import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'myr-privacy-modal',
  templateUrl: './privacy-modal.component.html',
})
export class PrivacyModalComponent {

  constructor(private activeModal: NgbActiveModal) {}

  dismiss(): void {
    this.activeModal.dismiss();
  }
}
