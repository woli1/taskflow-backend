package com.taskflow.auth;

import com.taskflow.exception.ConflictException;
import com.taskflow.security.JwtService;
import com.taskflow.user.User;
import com.taskflow.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            throw new ConflictException("Cet email est déjà utilisé");
        }
        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPassword(encoder.encode(request.password()));
        users.save(user);
        return new AuthResponse(jwt.generateToken(email), user.getName(), email);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        User user = users.findByEmail(email)
                .filter(u -> encoder.matches(request.password(), u.getPassword()))
                .orElseThrow(() -> new BadCredentialsException("Email ou mot de passe incorrect"));
        return new AuthResponse(jwt.generateToken(email), user.getName(), email);
    }
}
