package be.ucll.myrecipe.server.repository;

import be.ucll.myrecipe.server.domain.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @EntityGraph(attributePaths = "user")
    Optional<Recipe> findOneWithUserById(Long id);

    Page<Recipe> findAllByUserLogin(String userLogin, Pageable pageable);

}
