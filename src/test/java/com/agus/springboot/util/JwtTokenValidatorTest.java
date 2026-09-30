package com.agus.springboot.util;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.filters.ExpiresFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class JwtTokenValidatorTest {

    @Mock
    JwtUtils jwtUtils;

    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    FilterChain filterChain;

    @InjectMocks
    private JwtTokenValidator jwtTokenValidator;

    @BeforeEach
    void setUp() {
//        clear the security context before each test to isolate the tests
        SecurityContextHolder.clearContext();

    }

    @Test
    @DisplayName("FAIL - doFilterInternal - Does not authenticate when Authorization header is missing")
    void doFilterInternal_ShouldNotAuthenticate_WhenAuthorizationHeaderIsMissing() throws ServletException, IOException {

        Mockito.when(request.getHeader("Authorization")).thenReturn(null);

        jwtTokenValidator.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtUtils);

    }

    @Test
    @DisplayName("SUCCESS - doFilterInternal - Authenticates user when token is valid")
    void doFilterInternal_ShouldAuthenticate_WhenTokenIsValid() throws ServletException, IOException {
        String token = "valid_token_123";
        String username = "sam";

        Mockito.when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        Mockito.when(jwtUtils.validateToken(token)).thenReturn(true);
        Mockito.when(jwtUtils.extractUsername(token)).thenReturn(username);

        Claims claims = Mockito.mock(Claims.class);
        Mockito.when(claims.get("authorities", String.class)).thenReturn("ROLE_ADMIN");
        Mockito.when(jwtUtils.getClaims(token)).thenReturn(claims);

        jwtTokenValidator.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(username, authentication.getName());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

        verify(filterChain, times(1)).doFilter(request, response);
    }

}
