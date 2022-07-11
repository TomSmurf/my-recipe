package be.ucll.myrecipe.server.rest;

import be.ucll.myrecipe.server.api.UserCreationDto;
import be.ucll.myrecipe.server.api.UserUpdateDto;
import be.ucll.myrecipe.server.domain.Authority;
import be.ucll.myrecipe.server.domain.User;
import be.ucll.myrecipe.server.repository.AuthorityRepository;
import be.ucll.myrecipe.server.repository.UserRepository;
import be.ucll.myrecipe.server.security.AuthoritiesConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(value = "test", roles = "ADMIN")
class UserControllerTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Test
    void createUser() throws Exception {
        var userDto = new UserCreationDto();
        userDto.setLogin("jane");
        userDto.setFirstName("Jane");
        userDto.setLastName("Doe");
        userDto.setEmail("jane.doe@ucll.com");
        userDto.setAuthorities(Set.of(AuthoritiesConstants.ADMIN));

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userDto)))
                .andExpect(status().isCreated());

        var user = userRepository.findOneByLogin("jane").orElseThrow();
        assertThat(user.getLogin()).isEqualTo("jane");
        assertThat(user.getFirstName()).isEqualTo("Jane");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getEmail()).isEqualTo("jane.doe@ucll.com");
    }

    @Test
    void createUserWithExistingLogin() throws Exception {
        var user = new User();
        user.setLogin("test");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@ucll.com");
        user.setPassword("password");
        userRepository.save(user);

        var userDto = new UserCreationDto();
        userDto.setLogin("test");
        userDto.setFirstName("Jane");
        userDto.setLastName("Doe");
        userDto.setEmail("jane.doe@ucll.com");

        var databaseSizeBeforeCreate = userRepository.findAll().size();

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userDto)))
                .andExpect(status().isBadRequest());

        assertThat(userRepository.findAll()).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void createUserWithExistingEmail() throws Exception {
        var user = new User();
        user.setLogin("test");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@ucll.com");
        user.setPassword("password");
        userRepository.save(user);

        var userDto = new UserCreationDto();
        userDto.setLogin("jane");
        userDto.setFirstName("Jane");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@ucll.com");

        var databaseSizeBeforeCreate = userRepository.findAll().size();

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userDto)))
                .andExpect(status().isBadRequest());

        assertThat(userRepository.findAll()).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllUsers() throws Exception {
        var user = new User();
        user.setLogin("test");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@ucll.com");
        user.setPassword("password");
        userRepository.save(user);

        mockMvc.perform(get("/api/admin/users?sort=id,desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.content[*].login").value(hasItem("test")))
                .andExpect(jsonPath("$.content[*].firstName").value(hasItem("John")))
                .andExpect(jsonPath("$.content[*].lastName").value(hasItem("Doe")))
                .andExpect(jsonPath("$.content[*].email").value(hasItem("john.doe@ucll.com")));
    }

    @Test
    void getUser() throws Exception {
        var user = new User();
        user.setLogin("test");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@ucll.com");
        user.setPassword("password");
        userRepository.save(user);

        mockMvc.perform(get("/api/admin/users/{login}", user.getLogin()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.login").value("test"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@ucll.com"));
    }

    @Test
    void getNonExistingUser() throws Exception {
        mockMvc.perform(get("/api/admin/users/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser() throws Exception {
        var user = new User();
        user.setLogin("test");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@ucll.com");
        user.setPassword("password");
        userRepository.save(user);

        var userDto = new UserUpdateDto();
        userDto.setFirstName("Jane");
        userDto.setLastName("Doe");
        userDto.setEmail("jane.doe@ucll.com");
        userDto.setAuthorities(Set.of(AuthoritiesConstants.USER));

        mockMvc.perform(put("/api/admin/users/{login}", user.getLogin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userDto)))
                .andExpect(status().isNoContent());

        var updatedUser = userRepository.findOneByLogin("test").orElseThrow();
        assertThat(updatedUser.getLogin()).isEqualTo("test");
        assertThat(updatedUser.getFirstName()).isEqualTo("Jane");
        assertThat(updatedUser.getLastName()).isEqualTo("Doe");
        assertThat(updatedUser.getEmail()).isEqualTo("jane.doe@ucll.com");
    }

    @Test
    void deleteUser() throws Exception {
        var user = new User();
        user.setLogin("test");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@ucll.com");
        user.setPassword("password");
        userRepository.save(user);

        var databaseSizeBeforeDelete = userRepository.findAll().size();

        mockMvc.perform(delete("/api/admin/users/{login}", user.getLogin())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findAll()).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    void getAuthorities() throws Exception {
        var authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorityRepository.save(authority);

        mockMvc.perform(get("/api/admin/users/authorities")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").value(contains(authority.getName())));
    }
}
