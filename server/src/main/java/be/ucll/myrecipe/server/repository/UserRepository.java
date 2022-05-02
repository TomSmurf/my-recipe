package be.ucll.myrecipe.server.repository;

import be.ucll.myrecipe.server.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByLogin(String login);

    Optional<User> findOneByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    boolean existsByLogin(String login);

    boolean existsByEmailIgnoreCase(String email);

    @Query(value = "SELECT u.id FROM User u")
    Page<Long> findAllWithIdBy(Pageable pageable);

    @Query(value = "SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.authorities " +
            "WHERE u.id IN (?1)")
    List<User> findAllByIdIn(List<Long> ids, Sort pageable);

}
