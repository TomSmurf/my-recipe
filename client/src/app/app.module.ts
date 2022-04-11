import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { BrowserModule, Title } from '@angular/platform-browser';
import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { NgxWebstorageModule } from 'ngx-webstorage';
import { NgbDateAdapter, NgbDatepickerConfig } from '@ng-bootstrap/ng-bootstrap';
import { AppRoutingModule } from './app-routing.module';
import { HomeModule } from './home/home.module';
import { fontAwesomeIcons } from './config/font-awesome-icons';
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { ErrorComponent } from './layouts/error/error.component';
import { httpInterceptorProviders } from './core/interceptor';
import { SharedModule } from './shared/shared.module';

@NgModule({
  imports: [
    BrowserModule,
    SharedModule,
    HomeModule,
    AppRoutingModule,
    HttpClientModule,
    NgxWebstorageModule.forRoot({ prefix: 'myr', separator: '-', caseSensitive: true }),
  ],
  providers: [
    Title,
    httpInterceptorProviders,
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, FooterComponent],
  bootstrap: [MainComponent],
})
export class AppModule {
  constructor(iconLibrary: FaIconLibrary, dpConfig: NgbDatepickerConfig) {
    iconLibrary.addIcons(...fontAwesomeIcons);
    dpConfig.minDate = { year: 1900, month: 1, day: 1 };
  }
}
