import { Component, Input } from '@angular/core';

@Component({
  selector: 'myr-item-count',
  template: ` <div>Showing {{ first }} - {{ second }} of {{ total }} items.</div> `,
})
export class ItemCountComponent {

  @Input() set params(params: { page?: number; totalItems?: number; itemsPerPage?: number }) {
    if (params.page && params.totalItems !== undefined && params.itemsPerPage) {
      this.first = (params.page - 1) * params.itemsPerPage + 1;
      this.second = params.page * params.itemsPerPage < params.totalItems ? params.page * params.itemsPerPage : params.totalItems;
    } else {
      this.first = undefined;
      this.second = undefined;
    }
    this.total = params.totalItems;
  }

  first?: number;
  second?: number;
  total?: number;
}
