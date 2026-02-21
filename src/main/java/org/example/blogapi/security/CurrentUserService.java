package org.example.blogapi.security;

import lombok.RequiredArgsConstructor;
import org.example.blogapi.domain.entity.User;
import org.example.blogapi.domain.repository.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User getCurrentUserOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("Unauthenticated request");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof User u) {
            return u;
        }

        if (principal instanceof UserDetails ud) {
            String email = ud.getUsername();
            return userRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found in DB: " + email));
        }

        if (principal instanceof String email) {
            return userRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found in DB: " + email));
        }

        throw new RuntimeException("Unsupported principal type: " + principal.getClass().getName());
    }
}