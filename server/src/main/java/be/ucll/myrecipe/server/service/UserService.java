package be.ucll.myrecipe.server.service;

import be.ucll.myrecipe.server.domain.Authority;
import be.ucll.myrecipe.server.domain.User;
import be.ucll.myrecipe.server.exception.EmailAlreadyUsedException;
import be.ucll.myrecipe.server.exception.EntityNotFoundException;
import be.ucll.myrecipe.server.exception.InvalidPasswordException;
import be.ucll.myrecipe.server.exception.UsernameAlreadyUsedException;
import be.ucll.myrecipe.server.repository.AuthorityRepository;
import be.ucll.myrecipe.server.repository.UserRepository;
import be.ucll.myrecipe.server.security.AuthoritiesConstants;
import be.ucll.myrecipe.server.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public User getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException(User.class, login));
    }

    @Transactional
    public User registerUser(String login, String firstName, String lastName, String email, String password) {
        if (userRepository.existsByLogin(login)) {
            throw new UsernameAlreadyUsedException();
        }

        if (email != null && userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyUsedException();
        }

        var user = new User();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        var authorities = new HashSet<Authority>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        user.setAuthorities(authorities);
        return userRepository.save(user);
    }

    @Transactional
    public User createUser(String login, String firstName, String lastName, String email, Set<String> authorities) {
        if (userRepository.existsByLogin(login)) {
            throw new UsernameAlreadyUsedException();
        }

        if (email != null && userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyUsedException();
        }

        var user = new User();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(login.toLowerCase()));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        if (authorities != null) {
            user.setAuthorities(authorities
                    .stream()
                    .map(authorityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet()));
        }
        return userRepository.save(user);
    }

    @Transactional
    public void updateAccount(String firstName, String lastName, String email) {
        var userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("Current user login not found"));
        checkEmail(email, userLogin);

        var user = userRepository.findOneByLogin(userLogin).orElseThrow(() -> new IllegalStateException("User could not be found"));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
    }

    @Transactional
    public void updateUser(String login, String firstName, String lastName, String email, Set<String> authorities) {
        var user = userRepository.findOneByLogin(login).orElseThrow(() -> new EntityNotFoundException(User.class, login));
        checkEmail(email, login);

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        user.getAuthorities().clear();
        if (authorities != null) {
            for (var authority : authorities) {
                authorityRepository.findById(authority).ifPresent((a) -> user.getAuthorities().add(a));
            }
        }
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

    @Transactional
    public void deleteAccount() {
        var userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("Current user login not found"));
        userRepository.findOneByLogin(userLogin).ifPresent(userRepository::delete);
    }

    @Transactional
    public void deleteUser(String login) {
        userRepository.findOneByLogin(login).ifPresent(userRepository::delete);
    }

    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        var page = userRepository.findAllWithIdBy(pageable);
        var users = userRepository.findAllByIdIn(page.getContent(), pageable.getSort());
        return new PageImpl<>(users, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<String> getAuthorities() {
        return authorityRepository.findAll()
                .stream()
                .map(Authority::getName)
                .collect(Collectors.toList());
    }

    private void checkEmail(String email, String login) {
        if (email == null) {
            return;
        }
        userRepository.findOneByEmailIgnoreCase(email).ifPresent(u -> {
            if (!u.getLogin().equalsIgnoreCase(login)) {
                throw new EmailAlreadyUsedException();
            }
        });
    }
}
