import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PasswordStrengthBarComponent } from './password/password-strength-bar/password-strength-bar.component';
import { accountState } from './account.route';
import { SharedModule } from '../shared/shared.module';
import { PasswordComponent } from './password/password.component';
import { RegisterComponent } from './register/register.component';
import { SettingsComponent } from './settings/settings.component';

@NgModule({
  imports: [SharedModule, RouterModule.forChild(accountState)],
  declarations: [
    RegisterComponent,
    PasswordComponent,
    PasswordStrengthBarComponent,
    SettingsComponent,
  ],
})
export class AccountModule {}
