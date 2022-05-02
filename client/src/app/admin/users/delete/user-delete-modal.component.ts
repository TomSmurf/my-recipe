import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { User } from '../user.model';
import { UserService } from '../service/user.service';

@Component({
  selector: 'myr-user-delete-modal',
  templateUrl: './user-delete-modal.component.html',
})
export class UserDeleteModalComponent {
  user?: User;

  constructor(private userService: UserService, private activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(login: string): void {
    this.userService.delete(login).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
