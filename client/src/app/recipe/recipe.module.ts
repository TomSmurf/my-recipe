import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SharedModule } from '../shared/shared.module';
import { RecipeDeleteModalComponent } from './delete/recipe-delete-modal.component';
import { RecipeDetailComponent } from './detail/recipe-detail.component';
import { RecipeComponent } from './list/recipe.component';
import { recipeRoute } from './recipe.route';
import { RecipeUpdateComponent } from './update/recipe-update.component';

@NgModule({
    imports: [SharedModule, RouterModule.forChild(recipeRoute)],
    declarations: [
        RecipeComponent,
        RecipeUpdateComponent,
        RecipeDetailComponent,
        RecipeDeleteModalComponent
    ],
})
export class RecipeModule { }
