import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IRecipe, RecipeSearch } from '../recipe.model';
import { createRequestOption } from 'src/app/core/request/request-util';
import { Page } from 'src/app/core/request/request.model';

@Injectable({ providedIn: 'root' })
export class RecipeService {

  constructor(private http: HttpClient) { }

  create(recipe: IRecipe): Observable<IRecipe> {
    return this.http.post<IRecipe>('api/recipes', recipe);
  }

  update(recipe: IRecipe): Observable<IRecipe> {
    return this.http.put<IRecipe>(`api/recipes/${recipe.id}`, recipe);
  }

  find(id: number): Observable<IRecipe> {
    return this.http.get<IRecipe>(`api/recipes/${id}`);
  }

  download(id: number): any {
    return this.http.get(`api/recipes/${id}/pdf`, { responseType: 'blob' });
  }

  query(req?: RecipeSearch): Observable<Page<IRecipe>> {
    const options = createRequestOption(req);
    return this.http.get<Page<IRecipe>>('api/recipes', { params: options });
  }

  delete(id: number): Observable<{}> {
    return this.http.delete(`api/recipes/${id}`);
  }
}
