package com.agus.springboot.service;

import com.agus.springboot.dto.AuthLoginRequest;
import com.agus.springboot.dto.AuthResponse;
import com.agus.springboot.model.dao.IUserDAO;
import com.agus.springboot.model.entities.security.RoleEntity;
import com.agus.springboot.model.entities.security.RoleEnum;
import com.agus.springboot.model.entities.security.UserEntity;
import com.agus.springboot.service.impl.UserDetailServiceImpl;
import com.agus.springboot.util.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailServiceImplTest {
    @Mock
    IUserDAO userDAO;
    @Mock
    JwtUtils jwtUtils;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    UserDetailServiceImpl userDetailServiceImpl;

    @Test
    @DisplayName("SUCCESS - loadUserByUsername")
    void loadUserByUsername_Should(){
        String userName = "sam";
        String password = "1234";

        UserEntity dbUser = new UserEntity();
        dbUser.setUsername(userName);
        dbUser.setPassword(password);
        dbUser.setAccountNoExpired(true);
        dbUser.setAccountNoLocked(true);
        dbUser.setEnabled(true);
        dbUser.setCredentialNoExpired(true);

        RoleEntity roleAdmin = new RoleEntity();
        roleAdmin.setRoleEnum(RoleEnum.ADMIN);

        dbUser.getRoles().add(roleAdmin);

        Mockito.when(userDAO.findByUsername(userName)).thenReturn(Optional.of(dbUser));

        UserDetails result = userDetailServiceImpl.loadUserByUsername(userName);

        assertEquals(userName, result.getUsername());
        assertEquals(password, result.getPassword());
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isEnabled());
        assertTrue(result.isCredentialsNonExpired());

        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

        verify(userDAO, times(1)).findByUsername(userName);

    }

    @Test
    @DisplayName("FAIL - loadUserByUsername - Throws UsernameNotFoundException when user does not exist")
    void loadUserByUsername_ShouldThrowUsernameNotFoundException_WhenUserDoesNotExist(){
        String invalidUserName = "Not a user";

        Mockito.when(userDAO.findByUsername(invalidUserName)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailServiceImpl.loadUserByUsername(invalidUserName));

        verify(userDAO, times(1)).findByUsername(invalidUserName);

    }

    @Test
    @DisplayName("SUCCESS - loginUser - Returns AuthResponse with JWT when credentials are valid")
    void loginUser_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        String username = "sam";
        String rawPassword = "1234";

        AuthLoginRequest request = new AuthLoginRequest(username, rawPassword);

        RoleEntity role = new RoleEntity();
        role.setRoleEnum(RoleEnum.ADMIN);

        UserEntity dbUser = new UserEntity();
        dbUser.setUsername(username);
        dbUser.setPassword("encoded_1234");
        dbUser.getRoles().add(role);

        Mockito.when(userDAO.findByUsername(username)).thenReturn(Optional.of(dbUser));
        Mockito.when(passwordEncoder.matches(rawPassword, dbUser.getPassword())).thenReturn(true);
        Mockito.when(jwtUtils.generateToken(any(Authentication.class))).thenReturn("mock-jwt-token");

        AuthResponse response = userDetailServiceImpl.loginUser(request);

        assertNotNull(response);
        assertEquals(username, response.username());
        assertEquals("mock-jwt-token", response.jwt());
        assertTrue(response.status());

        verify(userDAO, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(rawPassword, "encoded_1234");
        verify(jwtUtils, times(1)).generateToken(any(Authentication.class));

    }


    @Test
    @DisplayName("Throws BadCredentialsException when password does not match")
    void loginUser_ShouldThrowBadCredentialsException_WhenPasswordIsInvalid(){
        String username = "sam";
        String badPassword = "9999";

        AuthLoginRequest request = new AuthLoginRequest(username, badPassword);

        RoleEntity role = new RoleEntity();
        role.setRoleEnum(RoleEnum.ADMIN);

        UserEntity dbUser = new UserEntity();
        dbUser.setUsername(username);
        dbUser.setPassword("1234");
        dbUser.getRoles().add(role);

        Mockito.when(userDAO.findByUsername(username)).thenReturn(Optional.of(dbUser));
        Mockito.when(passwordEncoder.matches(badPassword, dbUser.getPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> userDetailServiceImpl.loginUser(request));

        verify(userDAO, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(badPassword, dbUser.getPassword());
        verify(jwtUtils, never()).generateToken(any());

    }

}
