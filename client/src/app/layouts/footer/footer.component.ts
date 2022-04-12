import { Component } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CookiesModalComponent } from './modal/cookies-modal.component';
import { PrivacyModalComponent } from './modal/privacy-modal.component';

@Component({
  selector: 'myr-footer',
  templateUrl: './footer.component.html'
})
export class FooterComponent {

  constructor(private modalService: NgbModal) { }

  showPrivacy(): void {
    this.modalService.open(PrivacyModalComponent);
  }

  showCookies(): void {
    this.modalService.open(CookiesModalComponent);
  }
}
