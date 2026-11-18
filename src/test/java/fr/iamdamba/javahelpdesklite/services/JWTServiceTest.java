package fr.iamdamba.javahelpdesklite.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class JWTServiceTest {
    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setup() throws Exception {
        jwtService = new JwtService();

        setField(jwtService, "secret", "Qg7EFvpJxoJBuhxr00NSn++WJBzhFKcrrr3TfgMZTzU=");
        setField(jwtService, "expiration", 3600000L);

        userDetails = User.withUsername("test@test.test").password("test").authorities("ROLE_EMPLOYEE").build();
    }

    @Test
    void shouldGenerateTokenFromUsernameAndPassword() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals("test@test.test", jwtService.extractUsername(token));
    }

    @Test
    void shouldValidateTokenForCorrectUser() {
        // Prepare the token
        String token = jwtService.generateToken(userDetails);

        // Verify the token validation
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert the token is valid
        assertTrue(isValid);
    }

    @Test
    void shouldRejectTokenForDifferentUser() {
        // Prepare the token
        String token = jwtService.generateToken(userDetails);

        // Prepare the different user
        UserDetails differentUser = User.withUsername("other@other.com").password("other").authorities("ROLE_EMPLOYEE").build();

        // Verify the token validation
        boolean isValid = jwtService.isTokenValid(token, differentUser);

        // Assert the token is not valid
        assertFalse(isValid);
    }

    @Test
    void shouldRejectTamperedToken() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // On modifie le contenu du token
        String tamperedToken = token.substring(0, token.length() - 1)
                + (token.endsWith("a") ? "b" : "a");

        // Act & Assert
        assertFalse(jwtService.isTokenValid(tamperedToken, userDetails));
    }

    @Test
    void shouldRejectTokenForInvalidExpiration() throws Exception {
        JwtService wrongJwt = new JwtService();

        setField(wrongJwt, "secret", "Qg7EFvpJxoJBuhxr00NSn++WJBzhFKcrrr3TfgMZTzU=");
        setField(wrongJwt, "expiration", -1000L);

        UserDetails wrongDetails = User.withUsername("test@test.test").password("test").authorities("ROLE_EMPLOYEE").build();

        String token = wrongJwt.generateToken(wrongDetails);

        boolean isValid = jwtService.isTokenValid(token, wrongDetails);

        assertFalse(isValid);


    }

    private void setField(Object target, String fieldName, Object value)
            throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
