package fr.iamdamba.javahelpdesklite.services;

import fr.iamdamba.javahelpdesklite.entities.MemberEntity;
import fr.iamdamba.javahelpdesklite.models.dtos.auth.LoginDto;
import fr.iamdamba.javahelpdesklite.models.dtos.auth.RegisterDto;
import fr.iamdamba.javahelpdesklite.models.dtos.member.MemberDto;
import fr.iamdamba.javahelpdesklite.models.enums.MemberRoleEnum;
import fr.iamdamba.javahelpdesklite.repositories.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public ResponseEntity<String> login(LoginDto dto) {
        MemberEntity memberEntity = memberRepository.findByEmail(dto.email()).orElse(null);
        if (memberEntity == null)
            return new ResponseEntity<>("Email/Password not valid!", HttpStatus.UNAUTHORIZED);

        if (!passwordEncoder.matches(dto.password(), memberEntity.getPassword()))
            return new ResponseEntity<>("Email/Password not valid!", HttpStatus.UNAUTHORIZED);

        if (!memberEntity.isEnabled())
            return new ResponseEntity<>("Email/Password not valid!", HttpStatus.UNAUTHORIZED);

        String token = jwtService.generateToken(memberEntity);

        return ResponseEntity.ok(token);
    }

    public ResponseEntity<String> register(RegisterDto dto) {
        MemberEntity memberEntity = memberRepository.findByEmail(dto.email()).orElse(null);
        if (memberEntity != null)
            return new ResponseEntity<>("Email already exists!", HttpStatus.CONFLICT);

        MemberEntity newMember = MemberEntity.builder()
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .role(MemberRoleEnum.ADMIN)
                .isActive(true)
                .build();

        memberRepository.save(newMember);

        String token = jwtService.generateToken(newMember);
        return ResponseEntity.ok(token);
    }
}
