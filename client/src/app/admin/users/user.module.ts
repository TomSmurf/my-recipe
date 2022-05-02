import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SharedModule } from 'src/app/shared/shared.module';

import { UserDeleteModalComponent } from './delete/user-delete-modal.component';
import { UserDetailComponent } from './detail/user-detail.component';
import { UserComponent } from './list/user.component';
import { UserUpdateComponent } from './user-update.component';
import { userRoute } from './user.route';

@NgModule({
  imports: [SharedModule, RouterModule.forChild(userRoute)],
  declarations: [
    UserComponent,
    UserDetailComponent,
    UserDeleteModalComponent,
    UserUpdateComponent
  ],
  entryComponents: [UserDeleteModalComponent],
})
export class UserModule {}
