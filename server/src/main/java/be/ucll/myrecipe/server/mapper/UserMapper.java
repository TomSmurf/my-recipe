package be.ucll.myrecipe.server.mapper;

import be.ucll.myrecipe.server.api.AccountDto;
import be.ucll.myrecipe.server.api.UserDto;
import be.ucll.myrecipe.server.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = AuthorityMapper.class)
public interface UserMapper {

    AccountDto userToAccountDto(User user);

    UserDto userToUserDto(User user);

}
