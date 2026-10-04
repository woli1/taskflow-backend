package com.taskflow.security;

import com.taskflow.exception.NotFoundException;
import com.taskflow.user.User;
import com.taskflow.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Donne l'utilisateur connecté (déduit du JWT) aux services. */
@Component
@RequiredArgsConstructor
public class CurrentUser {

    private final UserRepository users;

    public User get() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return users.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable"));
    }
}
