package be.ucll.myrecipe.server.mapper;

import be.ucll.myrecipe.server.api.UserDto;
import be.ucll.myrecipe.server.domain.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToUserDto(User user);
}
