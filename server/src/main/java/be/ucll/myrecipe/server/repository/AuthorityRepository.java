package be.ucll.myrecipe.server.repository;

import be.ucll.myrecipe.server.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
