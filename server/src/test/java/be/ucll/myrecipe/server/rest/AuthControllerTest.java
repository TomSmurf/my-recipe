package be.ucll.myrecipe.server.rest;

import be.ucll.myrecipe.server.api.AuthDto;
import be.ucll.myrecipe.server.domain.User;
import be.ucll.myrecipe.server.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testAuthorize() throws Exception {
        var user = new User();
        user.setLogin("user-jwt-controller");
        user.setEmail("user-jwt-controller@example.com");
        user.setPassword(passwordEncoder.encode("test"));
        userRepository.save(user);

        var login = new AuthDto();
        login.setUsername("user-jwt-controller");
        login.setPassword("test");

        mockMvc.perform(post("/api/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_token").isString())
                .andExpect(jsonPath("$.id_token").isNotEmpty())
                .andExpect(header().string("Authorization", not(nullValue())))
                .andExpect(header().string("Authorization", not(is(emptyString()))));
    }

    @Test
    void testAuthorizeWithRememberMe() throws Exception {
        var user = new User();
        user.setLogin("user-jwt-controller-remember-me");
        user.setEmail("user-jwt-controller-remember-me@example.com");
        user.setPassword(passwordEncoder.encode("test"));
        userRepository.save(user);

        var login = new AuthDto();
        login.setUsername("user-jwt-controller-remember-me");
        login.setPassword("test");
        login.setRememberMe(true);

        mockMvc.perform(post("/api/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_token").isString())
                .andExpect(jsonPath("$.id_token").isNotEmpty())
                .andExpect(header().string("Authorization", not(nullValue())))
                .andExpect(header().string("Authorization", not(is(emptyString()))));
    }

    @Test
    void testAuthorizeFails() throws Exception {
        var login = new AuthDto();
        login.setUsername("wrong-user");
        login.setPassword("wrong password");

        mockMvc.perform(post("/api/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.id_token").doesNotExist())
                .andExpect(header().doesNotExist("Authorization"));
    }
}
