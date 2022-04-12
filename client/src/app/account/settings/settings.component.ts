import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Account } from 'src/app/core/auth/account.model';
import { AccountService } from 'src/app/core/auth/account.service';
import { LoginService } from 'src/app/login/login.service';
import { AccountDeleteModalComponent } from './modal/account-delete-modal.component';

@Component({
  selector: 'myr-settings',
  templateUrl: './settings.component.html',
})
export class SettingsComponent implements OnInit {
  account!: Account;
  success = false;

  settingsForm = this.fb.group({
    firstName: [undefined, [Validators.minLength(1), Validators.maxLength(50)]],
    lastName: [undefined, [Validators.minLength(1), Validators.maxLength(50)]],
    email: [undefined, [Validators.minLength(5), Validators.maxLength(254), Validators.email]]
  });

  constructor(
    private accountService: AccountService,
    private loginService: LoginService,
    private router: Router,
    private modalService: NgbModal,
    private fb: FormBuilder) { }

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => {
      if (account) {
        this.settingsForm.patchValue({
          firstName: account.firstName,
          lastName: account.lastName,
          email: account.email
        });

        this.account = account;
      }
    });
  }

  save(): void {
    this.success = false;

    this.account.firstName = this.settingsForm.get('firstName')!.value;
    this.account.lastName = this.settingsForm.get('lastName')!.value;
    this.account.email = this.settingsForm.get('email')!.value;

    this.accountService.save(this.account).subscribe(() => {
      this.success = true;
      this.accountService.authenticate(this.account);
    });
  }

  delete(): void {
    const modalRef = this.modalService.open(AccountDeleteModalComponent, { backdrop: 'static' });
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loginService.logout();
        this.router.navigate(['']);
      }
    });
  }
}
