export interface Pagination {
  page: number;
  size: number;
  sort: string[];
}

export interface Search {
  query: string;
}

export interface SearchWithPagination extends Search, Pagination {}

export interface Page<T> {
  content: T[],
  last: boolean,
  totalElements: number,
  totalPages: number,
  size: number,
  number: number,
  first: boolean,
  numberOfElements: number
}
