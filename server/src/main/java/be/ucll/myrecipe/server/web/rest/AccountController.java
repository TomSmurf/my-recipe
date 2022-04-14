package be.ucll.myrecipe.server.web.rest;

import be.ucll.myrecipe.server.api.AccountDto;
import be.ucll.myrecipe.server.api.AccountPasswordDto;
import be.ucll.myrecipe.server.api.AccountRegisterDto;
import be.ucll.myrecipe.server.api.AccountUpdateDto;
import be.ucll.myrecipe.server.mapper.UserMapper;
import be.ucll.myrecipe.server.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @PostMapping("/register")
    public void registerAccount(@Valid @RequestBody AccountRegisterDto userDto) {
        userService.registerUser(
                userDto.getLogin(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail(),
                userDto.getPassword()
        );
    }

    @GetMapping("/account")
    public AccountDto getAccount() {
        var user = userService.getUserWithAuthorities();
        return userMapper.userToUserDto(user);
    }

    @PostMapping("/account")
    public void saveAccount(@Valid @RequestBody AccountUpdateDto userDTO) {
        userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
    }

    @DeleteMapping("/account")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount() {
        userService.deleteUser();
    }

    @PostMapping("/account/change-password")
    public void changePassword(@Valid @RequestBody AccountPasswordDto accountPasswordDto) {
        userService.changePassword(accountPasswordDto.getCurrentPassword(), accountPasswordDto.getNewPassword());
    }
}
