package fr.iamdamba.javahelpdesklite.services;

import fr.iamdamba.javahelpdesklite.entities.MemberEntity;
import fr.iamdamba.javahelpdesklite.models.dtos.member.MemberDto;
import fr.iamdamba.javahelpdesklite.models.dtos.member.UpdateMemberDto;
import fr.iamdamba.javahelpdesklite.repositories.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public ResponseEntity<Page<MemberDto>> findAll(Pageable pageable) {
        return ResponseEntity.ok(memberRepository.findAll(pageable).map(MemberDto::fromEntity));
    }

    public ResponseEntity<MemberDto> findById(UUID id) {
        return memberRepository.findById(id)
                .map(member -> ResponseEntity.ok(MemberDto.fromEntity(member)))
                .orElse(null);
    }

    public ResponseEntity<MemberDto> update(UUID id, UpdateMemberDto dto) {
        MemberEntity memberEntity = memberRepository.findById(id).orElse(null);
        if (memberEntity == null)
            return ResponseEntity.badRequest().build();

        MemberEntity updateMember = memberEntity;
        updateMember.setFirstName(dto.firstName());
        updateMember.setLastName(dto.lastName());
        updateMember.setRole(dto.role());
        updateMember.setActive(dto.isActive());
        updateMember.setPassword(dto.password());
        updateMember.setEmail(dto.email());

        memberRepository.save(updateMember);
        return ResponseEntity.ok(MemberDto.fromEntity(updateMember));
    }

    public ResponseEntity<Void> deleteMember(UUID id) {
        if (memberRepository.findById(id).isEmpty())
            return ResponseEntity.notFound().build();

        memberRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
