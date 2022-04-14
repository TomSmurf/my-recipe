import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Recipe } from '../recipe.model';
import { RecipeService } from '../service/recipe.service';

@Component({
  selector: 'myr-recipe-update',
  templateUrl: './recipe-update.component.html',
  styleUrls: ['./recipe-update.component.scss'],
})
export class RecipeUpdateComponent implements OnInit {
  recipe!: Recipe;
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(255)]],
    rating: ['', [Validators.required, Validators.min(1), Validators.max(5)]],
    ingredients: ['', [Validators.required]],
    directions: ['', [Validators.required]],
    imageUrl: ['', [Validators.minLength(1), Validators.maxLength(255)]]
  });

  constructor(private recipeService: RecipeService, private route: ActivatedRoute, private fb: FormBuilder) { }

  ngOnInit(): void {
    this.route.data.subscribe(({ recipe }) => {
      if (recipe) {
        console.log(recipe);
        this.recipe = recipe;
        this.updateForm(recipe);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    this.updateRecipe(this.recipe);
    if (this.recipe.id !== undefined) {
      this.recipeService.update(this.recipe).subscribe({
        next: () => this.onSaveSuccess(),
        error: () => this.onSaveError(),
      });
    } else {
      this.recipeService.create(this.recipe).subscribe({
        next: () => this.onSaveSuccess(),
        error: () => this.onSaveError(),
      });
    }
  }

  private updateForm(recipe: Recipe): void {
    this.editForm.patchValue({
      id: recipe.id,
      name: recipe.name,
      rating: recipe.rating ?? 1,
      ingredients: recipe.ingredients,
      directions: recipe.directions,
      imageUrl: recipe.imageUrl
    });
  }

  private updateRecipe(recipe: Recipe): void {
    recipe.name = this.editForm.get(['name'])!.value;
    recipe.rating = this.editForm.get(['rating'])!.value;
    recipe.ingredients = this.editForm.get(['ingredients'])!.value;
    recipe.directions = this.editForm.get(['directions'])!.value;
    recipe.imageUrl = this.editForm.get(['imageUrl'])!.value;
  }

  private onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  private onSaveError(): void {
    this.isSaving = false;
  }
}
