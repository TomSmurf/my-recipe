package be.ucll.myrecipe.server.mapper;

import be.ucll.myrecipe.server.api.AccountDto;
import be.ucll.myrecipe.server.domain.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = AuthorityMapper.class)
public interface UserMapper {

    AccountDto userToUserDto(User user);
}
