package be.ucll.myrecipe.server.security.jwt;

import be.ucll.myrecipe.server.config.MyRecipeProperties;
import be.ucll.myrecipe.server.security.AuthoritiesConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenProviderTest {

    private TokenProvider tokenProvider;

    @BeforeEach
    public void init() {
        var myRecipeProperties = new MyRecipeProperties();
        myRecipeProperties.getSecurity().getAuthentication().getJwt().setBase64Secret("bXktc2VjcmV0LWtleS13aGljaC1zaG91bGQtYmUtY2hhbmdlZC1pbi1wcm9kdWN0aW9uLWFuZC1iZS1iYXNlNjQtZW5jb2RlZAo=");
        myRecipeProperties.getSecurity().getAuthentication().getJwt().setTokenValidityInSeconds(60000);
        myRecipeProperties.getSecurity().getAuthentication().getJwt().setTokenValidityInSecondsForRememberMe(60000);
        this.tokenProvider = new TokenProvider(myRecipeProperties);
    }

    @Test
    void testIsValid() {
        var authentication = new UsernamePasswordAuthenticationToken("test", "test", List.of(new SimpleGrantedAuthority(AuthoritiesConstants.USER)));
        var token = tokenProvider.createToken(authentication, false);

        assertThat(tokenProvider.validateToken(token)).isTrue();
    }

    @Test
    void testIsValidRememberMe() {
        var authentication = new UsernamePasswordAuthenticationToken("test", "test", List.of(new SimpleGrantedAuthority(AuthoritiesConstants.USER)));
        var token = tokenProvider.createToken(authentication, true);

        assertThat(tokenProvider.validateToken(token)).isTrue();
    }

    @Test
    void testIsInvalid() {
        assertThat(tokenProvider.validateToken("")).isFalse();
    }
}
