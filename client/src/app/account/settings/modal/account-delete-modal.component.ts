import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AccountService } from 'src/app/core/auth/account.service';

@Component({
  selector: 'myr-account-delete-modal',
  templateUrl: './account-delete-modal.component.html',
})
export class AccountDeleteModalComponent {

  constructor(private accountService: AccountService, private activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(): void {
    this.accountService.delete().subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
