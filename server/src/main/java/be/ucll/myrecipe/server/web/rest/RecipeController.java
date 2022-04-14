package be.ucll.myrecipe.server.web.rest;

import be.ucll.myrecipe.server.api.RecipeCreationDto;
import be.ucll.myrecipe.server.api.RecipeDto;
import be.ucll.myrecipe.server.api.RecipeUpdateDto;
import be.ucll.myrecipe.server.mapper.RecipeMapper;
import be.ucll.myrecipe.server.service.RecipeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api")
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeMapper recipeMapper;

    public RecipeController(RecipeService recipeService, RecipeMapper recipeMapper) {
        this.recipeService = recipeService;
        this.recipeMapper = recipeMapper;
    }

    @PostMapping("/recipes")
    public ResponseEntity<Object> createRecipe(@Valid @RequestBody RecipeCreationDto recipeDto) {
        var recipe = recipeService.createRecipe(
                recipeDto.getName(),
                recipeDto.getRating(),
                recipeDto.getIngredients(),
                recipeDto.getDirections(),
                recipeDto.getImageUrl());
        return ResponseEntity.created(URI.create("/api/recipes/" + recipe.getId())).build();
    }

    @PutMapping("/recipes/{id}")
    public ResponseEntity<Void> updateRecipe(@PathVariable Long id, @Valid @RequestBody RecipeUpdateDto recipeDto) {
        recipeService.updateRecipe(
                id,
                recipeDto.getName(),
                recipeDto.getRating(),
                recipeDto.getIngredients(),
                recipeDto.getDirections(),
                recipeDto.getImageUrl());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recipes")
    public Page<RecipeDto> getAllRecipes(Pageable pageable) {
        var recipes = recipeService.getAllRecipes(pageable);
        return recipes.map(recipeMapper::recipeToRecipeDto);
    }

    @GetMapping("/recipes/{id}")
    public ResponseEntity<RecipeDto> getRecipe(@PathVariable Long id) {
        var recipe = recipeService.getRecipe(id);
        return ResponseEntity.ok(recipeMapper.recipeToRecipeDto(recipe));
    }

    @DeleteMapping("/recipes/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }
}
