package fr.iamdamba.javahelpdesklite.services;

import fr.iamdamba.javahelpdesklite.entities.MemberEntity;
import fr.iamdamba.javahelpdesklite.models.dtos.auth.LoginDto;
import fr.iamdamba.javahelpdesklite.models.dtos.auth.RegisterDto;
import fr.iamdamba.javahelpdesklite.models.enums.MemberRoleEnum;
import fr.iamdamba.javahelpdesklite.repositories.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private MemberEntity member;

    @BeforeEach
    public void setUp() {
        member = MemberEntity.builder()
                .email("test@example.com")
                .password("hashed-password")
                .firstName("Test")
                .lastName("User")
                .role(MemberRoleEnum.EMPLOYEE)
                .isActive(true)
                .build();
    }

    // =====================================
    // Login
    // =====================================
    @Test
    void shouldLogedInSuccessfully() {
        LoginDto dto = new LoginDto(member.getEmail(), "plain-password");

        when(memberRepository.findByEmail(dto.email())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(dto.password(), member.getPassword())).thenReturn(true);
        when(jwtService.generateToken(member)).thenReturn("fake-jwt-token");

        // Act
        var response = authService.login(dto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("fake-jwt-token", response.getBody());

        verify(memberRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("plain-password", "hashed-password");
        verify(jwtService).generateToken(member);
    }

    @Test
    void shouldRejectWrongPassword() {
        LoginDto dto = new LoginDto(member.getEmail(), "wrong-password");

        when(memberRepository.findByEmail(dto.email())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(dto.password(), member.getPassword())).thenReturn(false);

        // Act
        var response = authService.login(dto);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Email/Password not valid!", response.getBody());

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void shouldRejectLoginWithUnknownEmail() {
        LoginDto dto = new LoginDto("wrong@email.test", "wrong-password");

        when(memberRepository.findByEmail(dto.email())).thenReturn(Optional.empty());

        // Act
        var response = authService.login(dto);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Email/Password not valid!", response.getBody());

        verify(memberRepository).findByEmail(dto.email());
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    void shouldRejectLoginForInactiveMember() {
        // Arrange
        member.setActive(false);

        LoginDto dto = new LoginDto(member.getEmail(), "plain-password");

        when(memberRepository.findByEmail(dto.email()))
                .thenReturn(Optional.of(member));

        when(passwordEncoder.matches(dto.password(), member.getPassword()))
                .thenReturn(true);

        // Act
        var response = authService.login(dto);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Email/Password not valid!", response.getBody());

        verify(passwordEncoder).matches(dto.password(), member.getPassword());
        verifyNoInteractions(jwtService);
    }

    // =====================================
    // Register
    // =====================================
    @Test
    void shouldRegisterSuccessfully() {
        // Arrange
        RegisterDto dto = new RegisterDto(
                "new@example.com",
                "plain-password",
                "New",
                "User"
        );

        when(memberRepository.findByEmail(dto.email()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(dto.password()))
                .thenReturn("hashed-password");

        when(jwtService.generateToken(any(MemberEntity.class)))
                .thenReturn("fake-jwt-token");

        // Act
        var response = authService.register(dto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("fake-jwt-token", response.getBody());

        verify(memberRepository).save(any(MemberEntity.class));
        verify(passwordEncoder).encode(dto.password());
        verify(jwtService).generateToken(any(MemberEntity.class));
    }

    @Test
    void shouldRejectRegistrationWithExistingEmail() {
        // Arrange
        RegisterDto dto = new RegisterDto(
                member.getEmail(),
                "plain-password",
                "Test",
                "User"
        );

        when(memberRepository.findByEmail(dto.email()))
                .thenReturn(Optional.of(member));

        // Act
        var response = authService.register(dto);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Email already exists!", response.getBody());

        verify(memberRepository, never()).save(any(MemberEntity.class));
        verifyNoInteractions(passwordEncoder, jwtService);
    }

}
