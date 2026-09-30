package com.agus.springboot.util;

import io.jsonwebtoken.Jwt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)

public class JwtUtilsTest {

    JwtUtils jwtUtils;

    @Mock
    Authentication authentication;

    private final String SECRET_KEY = "12345678901234567890123456789012";
    private final String USER_GENERATOR = "SpringBootApp";
    private final long EXPIRATION_TIME = 1800000; // 30 mins

    @BeforeEach
    void setUp(){
        this.jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtUtils, "userGenerator", USER_GENERATOR);
        ReflectionTestUtils.setField(jwtUtils, "expirationTimeMillis", EXPIRATION_TIME);
    }

    @Test
    @DisplayName("SUCCESS - generateToken & extractUsername")
    void generateToken_ShouldReturnValidToken() {
        // arrange
        String username = "sam";

        Mockito.when(authentication.getName()).thenReturn(username);
        Mockito.doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(authentication).getAuthorities();

        // act
        String token = jwtUtils.generateToken(authentication);

        // assert
        assertNotNull(token);
        assertTrue(jwtUtils.validateToken(token));
        assertEquals(username, jwtUtils.extractUsername(token));


    }

    @Test
    @DisplayName("FAIL - validateToken - Returns false when token is malformed")
    void validateToken_ShouldReturnFalse_WhenTokenIsInvalid(){
        String invalidToken = "invalidToken123";

        boolean isValid = jwtUtils.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("FAIL - validateToken - Returns false when token is expired")
    void validateToken_ShouldReturnFalse_When(){
        String username = "sam";
        ReflectionTestUtils.setField(jwtUtils, "expirationTimeMillis", -1000);

        Mockito.when(authentication.getName()).thenReturn(username);
        Mockito.doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(authentication).getAuthorities();

        String expiredToken = jwtUtils.generateToken(authentication);

        //act
        boolean isValid = jwtUtils.validateToken(expiredToken);

        assertFalse(isValid);

    }
}
