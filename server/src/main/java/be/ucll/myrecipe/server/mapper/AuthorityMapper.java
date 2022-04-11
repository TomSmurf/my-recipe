package be.ucll.myrecipe.server.mapper;

import be.ucll.myrecipe.server.domain.Authority;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface AuthorityMapper {

    default String authorityToName(Authority authority) {
        return authority.getName();
    }

    Set<String> authoritiesToNames(Set<Authority> authorities);
}
