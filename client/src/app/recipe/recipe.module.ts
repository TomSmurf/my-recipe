import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SharedModule } from '../shared/shared.module';
import { RecipeComponent } from './list/recipe.component';
import { recipeRoute } from './recipe.route';
import { RecipeUpdateComponent } from './update/recipe-update.component';

@NgModule({
    imports: [SharedModule, RouterModule.forChild(recipeRoute)],
    declarations: [
        RecipeComponent,
        RecipeUpdateComponent
    ],
})
export class RecipeModule { }
