import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { IRecipe, Recipe } from '../recipe.model';
import { ITEMS_PER_PAGE, ASC, DESC, SORT } from 'src/app/config/pagination.constants';
import { Account } from 'src/app/core/auth/account.model';
import { AccountService } from 'src/app/core/auth/account.service';
import { Page } from 'src/app/core/request/request.model';
import { RecipeService } from '../service/recipe.service';

@Component({
  selector: 'myr-recipe',
  templateUrl: './recipe.component.html',
})
export class RecipeComponent implements OnInit {
  currentAccount: Account | null = null;
  recipes: Recipe[] | null = null;
  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;
  predicate!: string;
  ascending!: boolean;

  constructor(
    private recipeService: RecipeService,
    private accountService: AccountService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal
  ) { }

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => (this.currentAccount = account));
    this.handleNavigation();
  }

  trackIdentity(_index: number, item: Recipe): number {
    return item.id!;
  }

  loadAll(): void {
    this.isLoading = true;
    this.recipeService
      .query({
        page: this.page - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: Page<Recipe>) => {
          this.isLoading = false;
          this.recipes = res.content;
          this.totalItems = res.totalElements;
        },
        error: () => (this.isLoading = false),
      });
  }

  transition(): void {
    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute.parent,
      queryParams: {
        page: this.page,
        sort: `${this.predicate},${this.ascending ? ASC : DESC}`,
      },
    });
  }

  private handleNavigation(): void {
    combineLatest([this.activatedRoute.data, this.activatedRoute.queryParamMap]).subscribe(([data, params]) => {
      const page = params.get('page');
      this.page = +(page ?? 1);
      const sort = (params.get(SORT) ?? data['defaultSort']).split(',');
      this.predicate = sort[0];
      this.ascending = sort[1] === ASC;
      this.loadAll();
    });
  }

  private sort(): string[] {
    const result = [`${this.predicate},${this.ascending ? ASC : DESC}`];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }
}
