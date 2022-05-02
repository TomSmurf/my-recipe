import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { createRequestOption } from 'src/app/core/request/request-util';
import { Page, Pagination } from 'src/app/core/request/request.model';
import { IUser } from '../user.model';

@Injectable({ providedIn: 'root' })
export class UserService {

  constructor(private http: HttpClient) { }

  create(user: IUser): Observable<{}> {
    return this.http.post<IUser>('api/admin/users', user);
  }

  update(user: IUser): Observable<{}> {
    return this.http.put<IUser>(`api/admin/users/${user.login}`, user);
  }

  find(login: string): Observable<IUser> {
    return this.http.get<IUser>(`api/admin/users/${login}`);
  }

  query(req?: Pagination): Observable<Page<IUser>> {
    const options = createRequestOption(req);
    return this.http.get<Page<IUser>>('api/admin/users', { params: options });
  }

  delete(login: string): Observable<{}> {
    return this.http.delete(`api/admin/users/${login}`);
  }

  authorities(): Observable<string[]> {
    return this.http.get<string[]>('api/admin/users/authorities');
  }
}
