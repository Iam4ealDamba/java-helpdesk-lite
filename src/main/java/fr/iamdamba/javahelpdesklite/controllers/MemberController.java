package fr.iamdamba.javahelpdesklite.controllers;

import fr.iamdamba.javahelpdesklite.models.dtos.member.MemberDto;
import fr.iamdamba.javahelpdesklite.models.dtos.member.UpdateMemberDto;
import fr.iamdamba.javahelpdesklite.services.MemberService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<Page<MemberDto>> all(@PageableDefault(size = 10) Pageable pageable) {
        return memberService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDto> one(@PathVariable UUID id) {
        return memberService.findById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberDto> update(@PathVariable UUID id, @RequestBody UpdateMemberDto dto) {
        return memberService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return memberService.deleteMember(id);
    }
}
