package be.ucll.myrecipe.server.rest;

import be.ucll.myrecipe.server.api.RecipeCreationDto;
import be.ucll.myrecipe.server.api.RecipeUpdateDto;
import be.ucll.myrecipe.server.domain.Recipe;
import be.ucll.myrecipe.server.domain.User;
import be.ucll.myrecipe.server.repository.RecipeRepository;
import be.ucll.myrecipe.server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RecipeControllerTest extends AbstractIntegrationTest {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void init() {
        var user = new User();
        user.setLogin("test");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@ucll.com");
        user.setPassword("password");
        this.user = userRepository.save(user);
    }

    @Test
    @WithMockUser("test")
    void testCreateValid() throws Exception {
        var recipeDto = new RecipeCreationDto();
        recipeDto.setName("Carrot Cake");
        recipeDto.setIngredients("2 cups white sugar");
        recipeDto.setDirections("Bake for 55 minutes");
        recipeDto.setRating(5);
        recipeDto.setImageUrl("http://localhost");

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(recipeDto)))
                .andExpect(status().isCreated());

        var recipes = recipeRepository.findAll();
        assertThat(recipes).hasSize(1);

        var recipe = recipes.iterator().next();
        assertThat(recipe.getName()).isEqualTo(recipeDto.getName());
        assertThat(recipe.getIngredients()).isEqualTo(recipeDto.getIngredients());
        assertThat(recipe.getDirections()).isEqualTo(recipeDto.getDirections());
        assertThat(recipe.getRating()).isEqualTo(recipeDto.getRating());
        assertThat(recipe.getImageUrl()).isEqualTo(recipeDto.getImageUrl());
    }

    @Test
    @WithMockUser("test")
    void testUpdateValid() throws Exception {
        var recipe = new Recipe();
        recipe.setName("Cake");
        recipe.setIngredients("3 eggs");
        recipe.setDirections("Mix eggs");
        recipe.setRating(1);
        recipe.setImageUrl("http://placehold.it");
        recipe.setUser(user);
        recipeRepository.save(recipe);

        var recipeDto = new RecipeUpdateDto();
        recipeDto.setName("Carrot Cake");
        recipeDto.setIngredients("2 cups white sugar");
        recipeDto.setDirections("Bake for 55 minutes");
        recipeDto.setRating(5);
        recipeDto.setImageUrl("http://localhost");

        mockMvc.perform(put("/api/recipes/{id}", recipe.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(recipeDto)))
                .andExpect(status().isNoContent());

        var updatedRecipe = recipeRepository.findById(recipe.getId()).orElseThrow();
        assertThat(updatedRecipe.getId()).isEqualTo(recipe.getId());
        assertThat(updatedRecipe.getName()).isEqualTo(recipeDto.getName());
        assertThat(updatedRecipe.getIngredients()).isEqualTo(recipeDto.getIngredients());
        assertThat(updatedRecipe.getDirections()).isEqualTo(recipeDto.getDirections());
        assertThat(updatedRecipe.getRating()).isEqualTo(recipeDto.getRating());
        assertThat(updatedRecipe.getImageUrl()).isEqualTo(recipeDto.getImageUrl());
    }

    @Test
    @WithMockUser("test")
    void testGetExistingRecipe() throws Exception {
        var recipe = new Recipe();
        recipe.setName("Cake");
        recipe.setIngredients("3 eggs");
        recipe.setDirections("Mix eggs");
        recipe.setRating(1);
        recipe.setImageUrl("http://placehold.it");
        recipe.setUser(user);
        recipeRepository.save(recipe);

        mockMvc.perform(get("/api/recipes/{id}", recipe.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(recipe.getId()))
                .andExpect(jsonPath("$.name").value(recipe.getName()))
                .andExpect(jsonPath("$.ingredients").value(recipe.getIngredients()))
                .andExpect(jsonPath("$.directions").value(recipe.getDirections()))
                .andExpect(jsonPath("$.rating").value(recipe.getRating()))
                .andExpect(jsonPath("$.imageUrl").value(recipe.getImageUrl()));
    }

    @Test
    @WithMockUser("test")
    void testDeleteRecipe() throws Exception {
        var recipe = new Recipe();
        recipe.setName("Cake");
        recipe.setIngredients("3 eggs");
        recipe.setDirections("Mix eggs");
        recipe.setRating(1);
        recipe.setImageUrl("http://placehold.it");
        recipe.setUser(user);
        recipeRepository.save(recipe);

        mockMvc.perform(delete("/api/recipes/{id}", recipe.getId()))
                .andExpect(status().isNoContent());

        var deletedRecipe = userRepository.findById(recipe.getId());
        assertThat(deletedRecipe).isEmpty();
    }

    @Test
    @WithMockUser("test")
    void testGetAllRecipes() throws Exception {
        var recipe = new Recipe();
        recipe.setName("Cake");
        recipe.setIngredients("3 eggs");
        recipe.setDirections("Mix eggs");
        recipe.setRating(1);
        recipe.setImageUrl("http://placehold.it");
        recipe.setUser(user);
        recipeRepository.save(recipe);

        mockMvc.perform(get("/api/recipes")
                        .param("sort", "id,desc")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.content[*].name").value(recipe.getName()))
                .andExpect(jsonPath("$.content[*].ingredients").value(recipe.getIngredients()))
                .andExpect(jsonPath("$.content[*].directions").value(recipe.getDirections()))
                .andExpect(jsonPath("$.content[*].rating").value(recipe.getRating()))
                .andExpect(jsonPath("$.content[*].imageUrl").value(recipe.getImageUrl()));
    }

    @Test
    @WithMockUser("test")
    void testGetRecipePdf() throws Exception {
        var recipe = new Recipe();
        recipe.setName("Cake");
        recipe.setIngredients("3 eggs");
        recipe.setDirections("Mix eggs");
        recipe.setRating(1);
        recipe.setImageUrl("http://placehold.it");
        recipe.setUser(user);
        recipeRepository.save(recipe);

        mockMvc.perform(get("/api/recipes/{id}/pdf", recipe.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

}
