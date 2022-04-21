package be.ucll.myrecipe.server.service;

import be.ucll.myrecipe.server.domain.Recipe;
import be.ucll.myrecipe.server.exception.EntityNotFoundException;
import be.ucll.myrecipe.server.exception.UserForbiddenException;
import be.ucll.myrecipe.server.repository.RecipeRepository;
import be.ucll.myrecipe.server.repository.UserRepository;
import be.ucll.myrecipe.server.security.SecurityUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Supplier;

@Service
public class RecipeService {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    private static final Font PARAGRAPH_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

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
    public Page<Recipe> getAllRecipes(String name, Integer rating, Pageable pageable) {
        var userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("Current user login not found"));
        return recipeRepository.findAllByUserLoginAndNameAndRating(userLogin, name, rating, pageable);
    }

    @Transactional(readOnly = true)
    public Resource getPdf(Long id) {
        var recipe = getRecipe(id);
        try (var baos = new ByteArrayOutputStream()) {
            var document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();
            addTitle(document, recipe.getName());
            addSubtitle(document, "Rating", () -> String.valueOf(recipe.getRating()));
            addSubtitle(document, "Ingredients", recipe::getIngredients);
            addSubtitle(document, "Directions", recipe::getDirections);
            document.close();
            return new ByteArrayResource(baos.toByteArray());
        } catch (DocumentException | IOException e) {
            throw new IllegalStateException("An error occurred creating pdf", e);
        }
    }

    private void addTitle(Document document, String title) throws DocumentException {
        document.add(new Paragraph(title, TITLE_FONT));
        document.add(Chunk.NEWLINE);
    }

    private void addSubtitle(Document document, String subtitle, Supplier<String> propertySupplier) throws DocumentException {
        document.add(new Paragraph(subtitle, SUBTITLE_FONT));
        document.add(new Paragraph(propertySupplier.get(), PARAGRAPH_FONT));
        document.add(Chunk.NEWLINE);
    }
}
