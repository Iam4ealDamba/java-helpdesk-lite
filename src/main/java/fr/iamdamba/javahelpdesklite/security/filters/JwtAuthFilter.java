package fr.iamdamba.javahelpdesklite.security.filters;

import fr.iamdamba.javahelpdesklite.entities.MemberEntity;
import fr.iamdamba.javahelpdesklite.repositories.MemberRepository;
import fr.iamdamba.javahelpdesklite.services.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            System.out.println("[JWT] Aucun token Bearer reçu");
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            String email = jwtService.extractUsername(token);
            System.out.println("[JWT] Email extrait : " + email);
            System.out.println("[JWT] Token reçu pour : " + email);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                MemberEntity entity = memberRepository.findByEmail(email).orElse(null);

                System.out.println("[JWT] Membre trouvé : " + (entity != null));

                if (entity != null) {
                    System.out.println("[JWT] Compte actif : " + entity.isEnabled());
                    System.out.println("[JWT] Rôles : " + entity.getAuthorities());
                    System.out.println("[JWT] Token valide : " + jwtService.isTokenValid(token, entity));
                }

                if (entity == null) {
                    System.out.println("[JWT] Aucun membre trouvé");
                } else if (!entity.isEnabled()) {
                    System.out.println("[JWT] Compte désactivé");
                } else if (!jwtService.isTokenValid(token, entity)) {
                    System.out.println("[JWT] Token invalide");
                } else {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    entity,
                                    null,
                                    entity.getAuthorities()
                            );

                    auth.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(auth);

                    System.out.println("[JWT] Authentification réussie : "
                            + entity.getAuthorities());
                }
            }
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("[JWT] Erreur de validation : " + e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
