import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Recipe } from '../recipe.model';
import { RecipeService } from '../service/recipe.service';

@Component({
  selector: 'myr-recipe-detail',
  templateUrl: './recipe-detail.component.html',
})
export class RecipeDetailComponent implements OnInit {
  recipe: Recipe | null = null;

  constructor(private recipeService: RecipeService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.data.subscribe(({ recipe }) => {
      this.recipe = recipe;
    });
  }

  download(id: number): void {
    this.recipeService.download(id).subscribe((response: BlobPart) => {
      let blob = new Blob([response], { type: 'application/pdf' });
      let url = window.URL.createObjectURL(blob);
      let pwa = window.open(url);
    });
  }
}
