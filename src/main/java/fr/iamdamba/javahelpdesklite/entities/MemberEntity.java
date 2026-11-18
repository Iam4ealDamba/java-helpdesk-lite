package fr.iamdamba.javahelpdesklite.entities;

import fr.iamdamba.javahelpdesklite.entities.generic.BaseEntity;
import fr.iamdamba.javahelpdesklite.models.enums.MemberRoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity extends BaseEntity implements UserDetails {
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Builder.Default
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRoleEnum role = MemberRoleEnum.EMPLOYEE;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    // ======================
    // Relationships
    // ======================
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketCommentEntity> comments;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketEntity> createdTickets;

    @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketEntity> assignedTickets;

    @OneToMany(mappedBy = "performedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketHistoricEntity> ticketHistorics;

    // ======================
    // Override methods
    // ======================
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
