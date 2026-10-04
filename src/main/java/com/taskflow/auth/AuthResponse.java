package com.taskflow.auth;

public record AuthResponse(String token, String name, String email) {
}
