package fr.iamdamba.javahelpdesklite.security;

import fr.iamdamba.javahelpdesklite.models.enums.MemberRoleEnum;
import fr.iamdamba.javahelpdesklite.security.filters.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class FilterChainConfig {
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/api/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/swagger.html").permitAll()
                        // Members
                        .requestMatchers(HttpMethod.GET, "/api/members/**").hasAnyRole(MemberRoleEnum.SUPPORT.name(), MemberRoleEnum.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/api/members/**").hasAnyRole(MemberRoleEnum.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/members/**").hasAnyRole(MemberRoleEnum.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/members/**").hasAnyRole(MemberRoleEnum.ADMIN.name())
                        // Tickets
                        .requestMatchers(HttpMethod.GET, "/api/tickets/**").hasAnyRole(MemberRoleEnum.SUPPORT.name(), MemberRoleEnum.EMPLOYEE.name(), MemberRoleEnum.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/api/tickets/**").hasAnyRole(MemberRoleEnum.SUPPORT.name(), MemberRoleEnum.EMPLOYEE.name(), MemberRoleEnum.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/tickets/{id}").hasAnyRole(MemberRoleEnum.SUPPORT.name(), MemberRoleEnum.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/tickets/{id}/status").hasAnyRole(MemberRoleEnum.SUPPORT.name(), MemberRoleEnum.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/tickets/{id}/assign/").hasAnyRole(MemberRoleEnum.SUPPORT.name(), MemberRoleEnum.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/tickets/{id}/assign/{memberId}").hasAnyRole(MemberRoleEnum.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/tickets/**").hasAnyRole(MemberRoleEnum.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        ;
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilterRegistration(JwtAuthFilter filter) {
        FilterRegistrationBean<JwtAuthFilter> registration =
                new FilterRegistrationBean<>(filter);

        registration.setEnabled(false);

        return registration;
    }
}
