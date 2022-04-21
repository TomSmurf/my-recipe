package be.ucll.myrecipe.server.mapper;

import be.ucll.myrecipe.server.api.RecipeDto;
import be.ucll.myrecipe.server.domain.Recipe;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RecipeMapper {

    RecipeDto recipeToRecipeDto(Recipe recipe);
}
