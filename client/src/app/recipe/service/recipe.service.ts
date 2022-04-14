import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IRecipe } from '../recipe.model';
import { createRequestOption } from 'src/app/core/request/request-util';
import { Page, Pagination } from 'src/app/core/request/request.model';

@Injectable({ providedIn: 'root' })
export class RecipeService {

  constructor(private http: HttpClient) { }

  create(user: IRecipe): Observable<IRecipe> {
    return this.http.post<IRecipe>('api/recipes', user);
  }

  update(user: IRecipe): Observable<IRecipe> {
    return this.http.put<IRecipe>('api/recipes', user);
  }

  find(id: string): Observable<IRecipe> {
    return this.http.get<IRecipe>(`${'api/recipes'}/${id}`);
  }

  query(req?: Pagination): Observable<Page<IRecipe>> {
    const options = createRequestOption(req);
    return this.http.get<Page<IRecipe>>('api/recipes', { params: options });
  }

  delete(id: string): Observable<{}> {
    return this.http.delete(`${'api/recipes'}/${id}`);
  }
}
