package be.ucll.myrecipe.server.rest;

import be.ucll.myrecipe.server.api.AccountPasswordDto;
import be.ucll.myrecipe.server.api.AccountRegisterDto;
import be.ucll.myrecipe.server.api.AccountUpdateDto;
import be.ucll.myrecipe.server.domain.Authority;
import be.ucll.myrecipe.server.domain.User;
import be.ucll.myrecipe.server.repository.AuthorityRepository;
import be.ucll.myrecipe.server.repository.UserRepository;
import be.ucll.myrecipe.server.security.AuthoritiesConstants;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser("test")
    void testAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/authenticate")
                        .with(request -> {
                            request.setRemoteUser("test");
                            return request;
                        })
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("test"));
    }

    @Test
    @WithMockUser("test")
    void testGetExistingAccount() throws Exception {
        var authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorityRepository.save(authority);

        var user = new User();
        user.setLogin("test");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@ucll.com");
        user.setPassword("password");
        user.setAuthorities(Set.of(authority));
        userRepository.save(user);

        mockMvc.perform(get("/api/account")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.login").value("test"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@ucll.com"))
                .andExpect(jsonPath("$.authorities").value(AuthoritiesConstants.ADMIN));
    }

    @Test
    @WithMockUser("test")
    void testGetUnknownAccount() {
        assertThatThrownBy(() -> mockMvc.perform(get("/api/account")))
                .hasCauseInstanceOf(RuntimeException.class)
                .hasMessageContaining("User could not be found");
    }

    @Test
    void testRegisterValid() throws Exception {
        var userRegisterDto = new AccountRegisterDto();
        userRegisterDto.setLogin("test");
        userRegisterDto.setPassword("password");
        userRegisterDto.setFirstName("John");
        userRegisterDto.setLastName("Doe");
        userRegisterDto.setEmail("john.doe@ucll.com");

        assertThat(userRepository.findOneByLogin("test")).isEmpty();

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userRegisterDto)))
                .andExpect(status().isOk());

        assertThat(userRepository.findOneByLogin("test")).isPresent();
    }

    @Test
    void testRegisterInvalidLogin() throws Exception {
        var userRegisterDto = new AccountRegisterDto();
        userRegisterDto.setLogin("funky-log(n");
        userRegisterDto.setPassword("password");
        userRegisterDto.setFirstName("John");
        userRegisterDto.setLastName("Doe");
        userRegisterDto.setEmail("john.doe@ucll.com");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userRegisterDto)))
                .andExpect(status().isBadRequest());

        var users = userRepository.findAll();
        assertThat(users).isEmpty();
    }

    @Test
    void testRegisterInvalidEmail() throws Exception {
        var userRegisterDto = new AccountRegisterDto();
        userRegisterDto.setLogin("test");
        userRegisterDto.setPassword("password");
        userRegisterDto.setFirstName("John");
        userRegisterDto.setLastName("Doe");
        userRegisterDto.setEmail("invalid");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userRegisterDto)))
                .andExpect(status().isBadRequest());

        var users = userRepository.findAll();
        assertThat(users).isEmpty();
    }

    @Test
    void testRegisterInvalidPassword() throws Exception {
        var userRegisterDto = new AccountRegisterDto();
        userRegisterDto.setLogin("test");
        userRegisterDto.setPassword("123");
        userRegisterDto.setFirstName("John");
        userRegisterDto.setLastName("Doe");
        userRegisterDto.setEmail("invalid");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userRegisterDto)))
                .andExpect(status().isBadRequest());

        var users = userRepository.findAll();
        assertThat(users).isEmpty();
    }

    @Test
    void testRegisterNullPassword() throws Exception {
        var userRegisterDto = new AccountRegisterDto();
        userRegisterDto.setLogin(null);
        userRegisterDto.setPassword("123");
        userRegisterDto.setFirstName("John");
        userRegisterDto.setLastName("Doe");
        userRegisterDto.setEmail("invalid");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userRegisterDto)))
                .andExpect(status().isBadRequest());

        var users = userRepository.findAll();
        assertThat(users).isEmpty();
    }

    @Test
    void testRegisterDuplicateLogin() throws Exception {
        var authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorityRepository.save(authority);

        var user = new User();
        user.setLogin("test");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@ucll.com");
        user.setPassword("password");
        user.setAuthorities(Set.of(authority));
        userRepository.save(user);

        var userRegisterDto = new AccountRegisterDto();
        userRegisterDto.setLogin("test");
        userRegisterDto.setPassword("password");
        userRegisterDto.setFirstName("Jane");
        userRegisterDto.setLastName("Doe");
        userRegisterDto.setEmail("jane.doe@ucll.com");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userRegisterDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterDuplicateEmail() throws Exception {
        var authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorityRepository.save(authority);

        var user = new User();
        user.setLogin("john");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("test@ucll.com");
        user.setPassword("password");
        user.setAuthorities(Set.of(authority));
        userRepository.save(user);

        var userRegisterDto = new AccountRegisterDto();
        userRegisterDto.setLogin("jane");
        userRegisterDto.setPassword("password");
        userRegisterDto.setFirstName("Jane");
        userRegisterDto.setLastName("Doe");
        userRegisterDto.setEmail("test@ucll.com");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userRegisterDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser("test")
    void testSaveAccount() throws Exception {
        var authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorityRepository.save(authority);

        var user = new User();
        user.setLogin("test");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@ucll.com");
        user.setPassword("password");
        user.setAuthorities(Set.of(authority));
        userRepository.save(user);

        var userUpdateDto = new AccountUpdateDto();
        userUpdateDto.setFirstName("Jane");
        userUpdateDto.setLastName("Doe");
        userUpdateDto.setEmail("jane.doe@ucll.com");

        mockMvc.perform(post("/api/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userUpdateDto)))
                .andExpect(status().isOk());

        var updatedUser = userRepository.findOneWithAuthoritiesByLogin(user.getLogin()).orElseThrow();
        assertThat(updatedUser.getFirstName()).isEqualTo(userUpdateDto.getFirstName());
        assertThat(updatedUser.getLastName()).isEqualTo(userUpdateDto.getLastName());
        assertThat(updatedUser.getEmail()).isEqualTo(userUpdateDto.getEmail());
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(updatedUser.getAuthorities()).map(Authority::getName).containsExactly(AuthoritiesConstants.ADMIN);
    }

    @Test
    @WithMockUser("test")
    void testSaveInvalidEmail() throws Exception {
        var authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorityRepository.save(authority);

        var user = new User();
        user.setLogin("test");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@ucll.com");
        user.setPassword("password");
        user.setAuthorities(Set.of(authority));
        userRepository.save(user);

        var userUpdateDto = new AccountUpdateDto();
        userUpdateDto.setFirstName("John");
        userUpdateDto.setLastName("Doe");
        userUpdateDto.setEmail("invalid");

        mockMvc.perform(post("/api/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userUpdateDto)))
                .andExpect(status().isBadRequest());

        assertThat(userRepository.findOneByEmailIgnoreCase("invalid")).isNotPresent();
    }

    @Test
    @WithMockUser("user1")
    void testSaveExistingEmail() throws Exception {
        var authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorityRepository.save(authority);

        var user1 = new User();
        user1.setLogin("user1");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setEmail("john.doe@ucll.com");
        user1.setPassword("password");
        user1.setAuthorities(Set.of(authority));
        userRepository.save(user1);

        var user2 = new User();
        user2.setLogin("user2");
        user2.setFirstName("Jane");
        user2.setLastName("Doe");
        user2.setEmail("jane.doe@ucll.com");
        user2.setPassword("password");
        user2.setAuthorities(Set.of(authority));
        userRepository.save(user2);

        var userUpdateDto = new AccountUpdateDto();
        userUpdateDto.setFirstName("John");
        userUpdateDto.setLastName("Doe");
        userUpdateDto.setEmail("jane.doe@ucll.com");

        mockMvc.perform(post("/api/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userUpdateDto)))
                .andExpect(status().isBadRequest());

        var updatedUser = userRepository.findOneByLogin("user1").orElseThrow();
        assertThat(updatedUser.getEmail()).isEqualTo("john.doe@ucll.com");
    }

    @Test
    @WithMockUser("test")
    void testSaveExistingEmailAndLogin() throws Exception {
        var authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorityRepository.save(authority);

        var user = new User();
        user.setLogin("test");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@ucll.com");
        user.setPassword("password");
        user.setAuthorities(Set.of(authority));
        userRepository.save(user);

        var userUpdateDto = new AccountUpdateDto();
        userUpdateDto.setFirstName("John");
        userUpdateDto.setLastName("Doe");
        userUpdateDto.setEmail("john.doe@ucll.com");

        mockMvc.perform(post("/api/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userUpdateDto)))
                .andExpect(status().isOk());

        var updatedUser = userRepository.findOneByLogin("test").orElseThrow();
        assertThat(updatedUser.getEmail()).isEqualTo("john.doe@ucll.com");
    }

    @Test
    @WithMockUser("test")
    void testChangePasswordWrongExistingPassword() throws Exception {
        var authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorityRepository.save(authority);

        var user = new User();
        user.setLogin("test");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@ucll.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setAuthorities(Set.of(authority));
        userRepository.save(user);

        var passwordChangeDto = new AccountPasswordDto();
        passwordChangeDto.setCurrentPassword("wrong-password");
        passwordChangeDto.setNewPassword("new-password");

        mockMvc.perform(post("/api/account/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(passwordChangeDto)))
                .andExpect(status().isBadRequest());

        var updatedUser = userRepository.findOneByLogin("test").orElseThrow();
        assertThat(passwordEncoder.matches("new password", updatedUser.getPassword())).isFalse();
        assertThat(passwordEncoder.matches("password", updatedUser.getPassword())).isTrue();
    }

    @Test
    @WithMockUser("test")
    void testChangePassword() throws Exception {
        var authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorityRepository.save(authority);

        var user = new User();
        user.setLogin("test");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@ucll.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setAuthorities(Set.of(authority));
        userRepository.save(user);

        var passwordChangeDto = new AccountPasswordDto();
        passwordChangeDto.setCurrentPassword("password");
        passwordChangeDto.setNewPassword("new password");

        mockMvc.perform(post("/api/account/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(passwordChangeDto)))
                .andExpect(status().isOk());

        var updatedUser = userRepository.findOneByLogin("test").orElseThrow();
        assertThat(passwordEncoder.matches("new password", updatedUser.getPassword())).isTrue();
    }

    @Test
    @WithMockUser("test")
    void testChangePasswordTooSmall() throws Exception {
        var authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorityRepository.save(authority);

        var user = new User();
        user.setLogin("test");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@ucll.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setAuthorities(Set.of(authority));
        userRepository.save(user);

        var passwordChangeDto = new AccountPasswordDto();
        passwordChangeDto.setCurrentPassword("password");
        passwordChangeDto.setNewPassword("123");

        mockMvc.perform(post("/api/account/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(passwordChangeDto)))
                .andExpect(status().isBadRequest());

        var updatedUser = userRepository.findOneByLogin("test").orElseThrow();
        assertThat(passwordEncoder.matches("new password", updatedUser.getPassword())).isFalse();
        assertThat(passwordEncoder.matches("password", updatedUser.getPassword())).isTrue();
    }

    @Test
    @WithMockUser("test")
    void testChangePasswordTooLong() throws Exception {
        var authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorityRepository.save(authority);

        var user = new User();
        user.setLogin("test");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@ucll.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setAuthorities(Set.of(authority));
        userRepository.save(user);

        var passwordChangeDto = new AccountPasswordDto();
        passwordChangeDto.setCurrentPassword("password");
        passwordChangeDto.setNewPassword(RandomStringUtils.random(200));

        mockMvc.perform(post("/api/account/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(passwordChangeDto)))
                .andExpect(status().isBadRequest());

        var updatedUser = userRepository.findOneByLogin("test").orElseThrow();
        assertThat(passwordEncoder.matches("new password", updatedUser.getPassword())).isFalse();
        assertThat(passwordEncoder.matches("password", updatedUser.getPassword())).isTrue();
    }

    @Test
    @WithMockUser("test")
    void testChangePasswordEmpty() throws Exception {
        var authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorityRepository.save(authority);

        var user = new User();
        user.setLogin("test");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@ucll.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setAuthorities(Set.of(authority));
        userRepository.save(user);

        var passwordChangeDto = new AccountPasswordDto();
        passwordChangeDto.setCurrentPassword("password");
        passwordChangeDto.setNewPassword(null);

        mockMvc.perform(post("/api/account/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(passwordChangeDto)))
                .andExpect(status().isBadRequest());

        var updatedUser = userRepository.findOneByLogin("test").orElseThrow();
        assertThat(passwordEncoder.matches("new password", updatedUser.getPassword())).isFalse();
        assertThat(passwordEncoder.matches("password", updatedUser.getPassword())).isTrue();
    }

    @Test
    @WithMockUser("test")
    void testDeleteAccount() throws Exception {
        var authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorityRepository.save(authority);

        var user = new User();
        user.setLogin("test");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@ucll.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setAuthorities(Set.of(authority));
        userRepository.save(user);

        mockMvc.perform(delete("/api/account"))
                .andExpect(status().isNoContent());

        var deletedUser = userRepository.findOneByLogin("test");
        assertThat(deletedUser).isEmpty();
    }
}
