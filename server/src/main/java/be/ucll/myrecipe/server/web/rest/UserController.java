package be.ucll.myrecipe.server.web.rest;

import be.ucll.myrecipe.server.api.UserCreationDto;
import be.ucll.myrecipe.server.api.UserDto;
import be.ucll.myrecipe.server.api.UserUpdateDto;
import be.ucll.myrecipe.server.config.Constants;
import be.ucll.myrecipe.server.mapper.UserMapper;
import be.ucll.myrecipe.server.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/users")
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserCreationDto userDto) {
        var user = userService.createUser(
                userDto.getLogin(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail(),
                userDto.getAuthorities()
        );
        return ResponseEntity.created(URI.create("/api/admin/users/" + user.getLogin())).build();
    }

    @PutMapping("/users/{login}")
    public ResponseEntity<Void> updateUser(@PathVariable @Pattern(regexp = Constants.LOGIN_REGEX) String login,
                                           @Valid @RequestBody UserUpdateDto userDto) {
        userService.updateUser(login, userDto.getFirstName(), userDto.getLastName(), userDto.getEmail(), userDto.getAuthorities());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable).map(userMapper::userToUserDto);
    }

    @GetMapping("/users/{login}")
    public UserDto getUser(@PathVariable @Pattern(regexp = Constants.LOGIN_REGEX) String login) {
        var user = userService.getUserWithAuthoritiesByLogin(login);
        return userMapper.userToUserDto(user);
    }

    @DeleteMapping("/users/{login}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Pattern(regexp = Constants.LOGIN_REGEX) String login) {
        userService.deleteUser(login);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/authorities")
    public List<String> getAuthorities() {
        return userService.getAuthorities();
    }
}
