import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { IRecipe, Recipe } from '../recipe.model';
import { Account } from 'src/app/core/auth/account.model';
import { AccountService } from 'src/app/core/auth/account.service';
import { Page } from 'src/app/core/request/request.model';
import { RecipeService } from '../service/recipe.service';
import { RecipeDeleteModalComponent } from '../delete/recipe-delete-modal.component';
import { FormBuilder } from '@angular/forms';

@Component({
  selector: 'myr-recipe',
  templateUrl: './recipe.component.html',
  styleUrls: ['./recipe.component.scss'],
})
export class RecipeComponent implements OnInit {
  currentAccount: Account | null = null;
  recipes: Recipe[] = [];
  isLoading = false;
  itemsPerPage = 12;
  last!: boolean;
  page = 0;
  sort = ['id'];
  name!: string;
  rating!: number;

  searchForm = this.fb.group({
    name: [''],
    rating: ['1'],
    sort: ['name,asc']
  });

  constructor(
    private recipeService: RecipeService,
    private accountService: AccountService,
    private modalService: NgbModal,
    private fb: FormBuilder
  ) {
  }

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => (this.currentAccount = account));
    this.loadAll();
  }

  trackIdentity(_index: number, item: Recipe): number {
    return item.id!;
  }

  deleteRecipe(recipe: Recipe): void {
    const modalRef = this.modalService.open(RecipeDeleteModalComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.recipe = recipe;
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.reset();
      }
    });
  }

  loadAll(): void {
    this.isLoading = true;
    this.recipeService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort,
        name: this.name,
        rating: this.rating,
      })
      .subscribe({
        next: (res: Page<Recipe>) => {
          this.isLoading = false;
          this.last = res.last;
          this.paginateRecipes(res.content);
        },
        error: () => (this.isLoading = false),
      });
  }

  reset(): void {
    this.page = 0;
    this.recipes = [];
    this.sort = [this.searchForm.get(['sort'])!.value, 'id'];
    this.name = this.searchForm.get(['name'])?.value;
    this.rating = this.searchForm.get(['rating'])?.value;
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  protected paginateRecipes(data: IRecipe[] | null): void {
    if (data) {
      for (const d of data) {
        this.recipes.push(d);
      }
    }
  }
}
