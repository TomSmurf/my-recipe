package be.ucll.myrecipe.server.service;

import be.ucll.myrecipe.server.domain.Recipe;
import be.ucll.myrecipe.server.exception.EntityNotFoundException;
import be.ucll.myrecipe.server.exception.UserForbiddenException;
import be.ucll.myrecipe.server.repository.RecipeRepository;
import be.ucll.myrecipe.server.repository.UserRepository;
import be.ucll.myrecipe.server.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public RecipeService(RecipeRepository recipeRepository, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Recipe createRecipe(String name, int rating, String ingredients, String directions, String imageUrl) {
        var userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("Current user login not found"));
        var user = userRepository.findOneByLogin(userLogin).orElseThrow(() -> new IllegalStateException("User could not be found"));

        var recipe = new Recipe();
        recipe.setName(name);
        recipe.setRating(rating);
        recipe.setIngredients(ingredients);
        recipe.setDirections(directions);
        recipe.setImageUrl(imageUrl);
        recipe.setUser(user);
        return recipeRepository.save(recipe);
    }

    @Transactional
    public Recipe updateRecipe(Long id, String name, int rating, String ingredients, String directions, String imageUrl) {
        var userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("Current user login not found"));

        var recipe = recipeRepository.findOneWithUserById(id).orElseThrow(() -> new EntityNotFoundException(Recipe.class, id));

        if (!recipe.getUser().getLogin().equalsIgnoreCase(userLogin)) {
            throw new UserForbiddenException();
        }

        recipe.setName(name);
        recipe.setRating(rating);
        recipe.setIngredients(ingredients);
        recipe.setDirections(directions);
        recipe.setImageUrl(imageUrl);
        return recipeRepository.save(recipe);
    }

    @Transactional(readOnly = true)
    public Recipe getRecipe(Long id) {
        var userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("Current user login not found"));
        var recipe = recipeRepository.findOneWithUserById(id).orElseThrow(() -> new EntityNotFoundException(Recipe.class, id));
        if (!recipe.getUser().getLogin().equalsIgnoreCase(userLogin)) {
            throw new UserForbiddenException();
        }
        return recipe;
    }

    @Transactional
    public void deleteRecipe(Long id) {
        var userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("Current user login not found"));
        recipeRepository.findOneWithUserById(id).ifPresent(recipe -> {
            if (!recipe.getUser().getLogin().equalsIgnoreCase(userLogin)) {
                throw new UserForbiddenException();
            }
            recipeRepository.delete(recipe);
        });
    }

    @Transactional(readOnly = true)
    public Page<Recipe> getAllRecipes(Pageable pageable) {
        var userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("Current user login not found"));
        return recipeRepository.findAllByUserLogin(userLogin, pageable);
    }
}
