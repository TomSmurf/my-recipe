package be.ucll.myrecipe.server.security;

import be.ucll.myrecipe.server.domain.User;
import be.ucll.myrecipe.server.repository.UserRepository;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public DomainUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) {
        if (new EmailValidator().isValid(login, null)) {
            return userRepository
                    .findOneWithAuthoritiesByEmailIgnoreCase(login)
                    .map(this::createSpringSecurityUser)
                    .orElseThrow(() -> new UsernameNotFoundException("User with email " + login + " was not found in the database"));
        }

        var lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
        return userRepository
                .findOneWithAuthoritiesByLogin(lowercaseLogin)
                .map(this::createSpringSecurityUser)
                .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database"));
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(User user) {
        var grantedAuthorities = user
                .getAuthorities()
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .toList();
        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), grantedAuthorities);
    }
}
