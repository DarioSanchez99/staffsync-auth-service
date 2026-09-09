package com.staffsync.auth.domain.port.out;

import com.staffsync.auth.domain.model.User;

public interface TokenPort {
    String generateToken(User user);
    boolean validateToken(String token);
    String extractUserId(String token);
}
