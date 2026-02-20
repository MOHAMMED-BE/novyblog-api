package org.example.blogapi.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.blogapi.domain.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email", unique = true),
                @Index(name = "idx_users_enabled", columnList = "enabled")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends TraceableEntity implements UserDetails {

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, length = 180, unique = true)
    private String email;

    /**
     * Store only hashed passwords (BCrypt/Argon2). Never store raw password.
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * Admin can disable users/authors.
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    /**
     * Optional but useful for security/abuse control (e.g., lock user after many failed logins).
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean accountNonLocked = true;

    /**
     * Simple roles model for Spring Security.
     * Stored in a separate table: user_roles(user_id, role)
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            uniqueConstraints = @UniqueConstraint(name = "uniq_user_role", columnNames = {"user_id", "role"})
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    // -------------------------
    // Spring Security - UserDetails
    // -------------------------

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security expects roles in the form "ROLE_X"
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String getUsername() {
        // We use email as the login identifier
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // can be extended later if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // can be extended later if needed
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // -------------------------
    // Convenience helpers (optional)
    // -------------------------

    public boolean hasRole(Role role) {
        return roles != null && roles.contains(role);
    }
}