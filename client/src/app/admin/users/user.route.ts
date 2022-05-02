import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, Routes } from '@angular/router';
import { Observable, of } from 'rxjs';

import { User, IUser } from './user.model';
import { UserService } from './service/user.service';
import { UserDetailComponent } from './detail/user-detail.component';
import { UserComponent } from './list/user.component';
import { UserUpdateComponent } from './user-update.component';

@Injectable({ providedIn: 'root' })
export class UserManagementResolve implements Resolve<IUser> {
  constructor(private userService: UserService) { }

  resolve(route: ActivatedRouteSnapshot): Observable<IUser> {
    const id = route.params['login'];
    if (id) {
      return this.userService.find(id);
    }
    return of(new User());
  }
}

export const userRoute: Routes = [
  {
    path: '',
    component: UserComponent,
    data: {
      defaultSort: 'id,asc',
    },
  },
  {
    path: ':login/view',
    component: UserDetailComponent,
    resolve: {
      user: UserManagementResolve,
    },
  },
  {
    path: 'new',
    component: UserUpdateComponent,
    resolve: {
      user: UserManagementResolve,
    },
  },
  {
    path: ':login/edit',
    component: UserUpdateComponent,
    resolve: {
      user: UserManagementResolve,
    }
  }
];
