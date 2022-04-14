export interface IRecipe {
  id?: number;
  name?: string;
  rating?: number;
  ingredients?: string;
  directions?: string;
  imageUrl?: string | null;
  createdDate?: string;
  lastModifiedDate?: string;
}

export class Recipe implements IRecipe {
  constructor(
    public id?: number,
    public name?: string,
    public rating?: number,
    public ingredients?: string,
    public directions?: string,
    public imageUrl?: string | null,
    public createdDate?: string,
    public lastModifiedDate?: string
  ) { }
}
