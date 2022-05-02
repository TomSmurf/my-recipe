package be.ucll.myrecipe.server.web.rest;

import be.ucll.myrecipe.server.api.RecipeCreationDto;
import be.ucll.myrecipe.server.api.RecipeDto;
import be.ucll.myrecipe.server.api.RecipeUpdateDto;
import be.ucll.myrecipe.server.mapper.RecipeMapper;
import be.ucll.myrecipe.server.service.RecipeService;
import com.itextpdf.text.DocumentException;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
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
    public ResponseEntity<Void> createRecipe(@Valid @RequestBody RecipeCreationDto recipeDto) {
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
    public Page<RecipeDto> getAllRecipes(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) Integer rating,
                                         Pageable pageable) {
        var recipes = recipeService.getAllRecipes(name, rating, pageable);
        return recipes.map(recipeMapper::recipeToRecipeDto);
    }

    @GetMapping("/recipes/{id}")
    public ResponseEntity<RecipeDto> getRecipe(@PathVariable Long id) {
        var recipe = recipeService.getRecipe(id);
        return ResponseEntity.ok(recipeMapper.recipeToRecipeDto(recipe));
    }

    @GetMapping("/recipes/{id}/pdf")
    public ResponseEntity<Resource> getPdf(@PathVariable Long id) throws IOException, DocumentException {
        var resource = recipeService.getPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"myrecipe.pdf\"")
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @DeleteMapping("/recipes/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }
}
