package be.ucll.myrecipe.server.web.rest;

import be.ucll.myrecipe.server.api.PasswordChangeDto;
import be.ucll.myrecipe.server.api.UserDto;
import be.ucll.myrecipe.server.api.UserRegisterDto;
import be.ucll.myrecipe.server.api.UserUpdateDto;
import be.ucll.myrecipe.server.mapper.UserMapper;
import be.ucll.myrecipe.server.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AccountController {

    private final UserService userService;
    private final UserMapper userMapper;

    public AccountController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        return request.getRemoteUser();
    }

    @GetMapping("/account")
    public UserDto getAccount() {
        var user = userService.getUserWithAuthorities();
        return userMapper.userToUserDto(user);
    }

    @PostMapping("/register")
    public void registerAccount(@Valid @RequestBody UserRegisterDto userDto) {
        userService.registerUser(
                userDto.getLogin(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail(),
                userDto.getPassword()
        );
    }

    @PostMapping("/account")
    public void saveAccount(@Valid @RequestBody UserUpdateDto userDTO) {
        userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
    }

    @PostMapping("/account/change-password")
    public void changePassword(@Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }
}
