package be.ucll.myrecipe.server.service;

import be.ucll.myrecipe.server.domain.Authority;
import be.ucll.myrecipe.server.domain.User;
import be.ucll.myrecipe.server.exception.EmailAlreadyUsedException;
import be.ucll.myrecipe.server.exception.InvalidPasswordException;
import be.ucll.myrecipe.server.exception.UsernameAlreadyUsedException;
import be.ucll.myrecipe.server.repository.AuthorityRepository;
import be.ucll.myrecipe.server.repository.UserRepository;
import be.ucll.myrecipe.server.security.AuthoritiesConstants;
import be.ucll.myrecipe.server.security.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            AuthorityRepository authorityRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneWithAuthoritiesByLogin)
                .orElseThrow(() -> new IllegalStateException("User could not be found"));
    }

    @Transactional
    public User registerUser(String login, String firstName, String lastName, String email, String password) {
        if (userRepository.existsByLogin(login)) {
            throw new UsernameAlreadyUsedException();
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyUsedException();
        }

        var user = new User();
        user.setLogin(login.toLowerCase());
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);

        if (email != null) {
            user.setEmail(email.toLowerCase());
        }

        var authorities = new HashSet<Authority>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        user.setAuthorities(authorities);
        return userRepository.save(user);
    }

    @Transactional
    public void updateUser(String firstName, String lastName, String email) {
        var userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("Current user login not found"));

        if (email != null) {
            var userOpt = userRepository.findOneByEmailIgnoreCase(email);
            if (userOpt.isPresent() && (!userOpt.get().getLogin().equalsIgnoreCase(userLogin))) {
                throw new EmailAlreadyUsedException();
            }
        }

        var user = userRepository.findOneByLogin(userLogin).orElseThrow(() -> new IllegalStateException("User could not be found"));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email == null ? null : email.toLowerCase());
    }

    @Transactional
    public void changePassword(String currentPassword, String newPassword) {
        var userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("Current user login not found"));

        var user = userRepository.findOneByLogin(userLogin).orElseThrow(() -> new IllegalStateException("User could not be found"));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidPasswordException();
        }
        user.setPassword(passwordEncoder.encode(newPassword));
    }
}
