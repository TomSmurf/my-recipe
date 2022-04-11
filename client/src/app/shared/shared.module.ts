import { NgModule } from '@angular/core';
import { AlertErrorComponent } from './alert/alert-error.component';
import { HasAnyAuthorityDirective } from './auth/has-any-authority.directive';
import { ItemCountComponent } from './pagination/item-count.component';
import { SharedLibsModule } from './shared-libs.module';
import { SortByDirective } from './sort/sort-by.directive';
import { SortDirective } from './sort/sort.directive';

@NgModule({
  imports: [SharedLibsModule],
  declarations: [
    AlertErrorComponent,
    HasAnyAuthorityDirective,
    SortByDirective,
    SortDirective,
    ItemCountComponent,
  ],
  exports: [
    SharedLibsModule,
    AlertErrorComponent,
    HasAnyAuthorityDirective,
    SortByDirective,
    SortDirective,
    ItemCountComponent,
  ],
})
export class SharedModule {}
