package be.ucll.myrecipe.server.repository;

import be.ucll.myrecipe.server.domain.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomizedRecipeRepository {

    long countByUserLoginAndNameAndRating(String userLogin, String name, Integer rating);

    Page<Recipe> findAllByUserLoginAndNameAndRating(String userLogin, String name, Integer rating, Pageable pageable);

}
