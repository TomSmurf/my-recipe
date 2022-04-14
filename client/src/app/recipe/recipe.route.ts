import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, Routes } from '@angular/router';
import { Observable, of } from 'rxjs';
import { RecipeComponent } from './list/recipe.component';
import { IRecipe, Recipe } from './recipe.model';
import { RecipeService } from './service/recipe.service';
import { RecipeUpdateComponent } from './update/recipe-update.component';

@Injectable({ providedIn: 'root' })
export class RecipeResolve implements Resolve<IRecipe> {
    constructor(private recipeService: RecipeService) { }

    resolve(route: ActivatedRouteSnapshot): Observable<IRecipe> {
        const id = route.params['id'];
        if (id) {
            return this.recipeService.find(id);
        }
        return of(new Recipe());
    }
}

export const recipeRoute: Routes = [
    {
        path: '',
        component: RecipeComponent,
        data: {
            defaultSort: 'id,asc',
        },
    },
    {
        path: 'new',
        component: RecipeUpdateComponent,
        resolve: {
          recipe: RecipeResolve,
        },
      },
];
