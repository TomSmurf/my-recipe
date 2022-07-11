package be.ucll.myrecipe.server.security.jwt;

import be.ucll.myrecipe.server.config.MyRecipeProperties;
import be.ucll.myrecipe.server.security.AuthoritiesConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JwtFilterTest {

    private TokenProvider tokenProvider;

    private JwtFilter jwtFilter;

    @BeforeEach
    void init() {
        var myRecipeProperties = new MyRecipeProperties();
        myRecipeProperties.getSecurity().getAuthentication().getJwt().setBase64Secret("bXktc2VjcmV0LWtleS13aGljaC1zaG91bGQtYmUtY2hhbmdlZC1pbi1wcm9kdWN0aW9uLWFuZC1iZS1iYXNlNjQtZW5jb2RlZAo=");
        myRecipeProperties.getSecurity().getAuthentication().getJwt().setTokenValidityInSeconds(60000);
        myRecipeProperties.getSecurity().getAuthentication().getJwt().setTokenValidityInSecondsForRememberMe(60000);
        this.tokenProvider = new TokenProvider(myRecipeProperties);
        this.jwtFilter = new JwtFilter(tokenProvider);
    }

    @AfterEach
    void after() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    void testJWTFilter() throws Exception {
        var authentication = new UsernamePasswordAuthenticationToken("test", "test", List.of(new SimpleGrantedAuthority(AuthoritiesConstants.USER)));
        var token = tokenProvider.createToken(authentication, false);

        var request = new MockHttpServletRequest();
        request.addHeader(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + token);
        request.setRequestURI("/api/test");

        var response = new MockHttpServletResponse();

        jwtFilter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("test");
        assertThat(SecurityContextHolder.getContext().getAuthentication().getCredentials()).hasToString(token);
    }

    @Test
    void testJWTFilterInvalidToken() throws Exception {
        var request = new MockHttpServletRequest();
        request.addHeader(JwtFilter.AUTHORIZATION_HEADER, "Bearer wrong-jwt-token");
        request.setRequestURI("/api/test");

        var response = new MockHttpServletResponse();

        jwtFilter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

}
