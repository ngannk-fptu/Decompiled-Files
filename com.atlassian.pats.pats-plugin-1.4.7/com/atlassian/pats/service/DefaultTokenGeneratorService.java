/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pats.service;

import com.atlassian.pats.api.TokenGeneratorService;
import com.atlassian.pats.service.GeneratedToken;
import com.atlassian.security.password.PasswordEncoder;
import com.atlassian.security.random.SecureRandomService;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class DefaultTokenGeneratorService
implements TokenGeneratorService {
    private final PasswordEncoder passwordEncoder;
    private final SecureRandomService secureRandomService;

    public DefaultTokenGeneratorService(PasswordEncoder passwordEncoder, SecureRandomService secureRandomService) {
        this.passwordEncoder = passwordEncoder;
        this.secureRandomService = secureRandomService;
    }

    @Override
    public GeneratedToken createToken() {
        byte[] tokenBytes = new byte[33];
        byte[] id = this.generateId();
        System.arraycopy(id, 0, tokenBytes, 0, id.length);
        tokenBytes[id.length] = 58;
        byte[] secret = this.generateSecret();
        System.arraycopy(secret, 0, tokenBytes, id.length + 1, secret.length);
        String generatedBase64Token = Base64.getEncoder().encodeToString(tokenBytes);
        String hashedToken = this.passwordEncoder.encodePassword(Base64.getEncoder().encodeToString(secret));
        return new GeneratedToken(new String(id, StandardCharsets.UTF_8), hashedToken, generatedBase64Token);
    }

    private byte[] generateId() {
        StringBuilder idBuilder = new StringBuilder(12);
        for (int i = 0; i < 12; ++i) {
            idBuilder.append(this.secureRandomService.nextInt(10));
        }
        return idBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] generateSecret() {
        byte[] secret = new byte[20];
        this.secureRandomService.nextBytes(secret);
        return secret;
    }
}

