/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.internal;

import com.atlassian.jwt.Jwt;
import com.atlassian.jwt.JwtService;
import com.atlassian.jwt.SigningAlgorithm;
import com.atlassian.jwt.exception.JwtIssuerLacksSharedSecretException;
import com.atlassian.jwt.exception.JwtParseException;
import com.atlassian.jwt.exception.JwtUnknownIssuerException;
import com.atlassian.jwt.exception.JwtVerificationException;
import com.atlassian.jwt.reader.JwtClaimVerifier;
import com.atlassian.jwt.reader.JwtReaderFactory;
import com.atlassian.jwt.writer.JwtWriterFactory;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public class DefaultJwtService
implements JwtService {
    private final JwtReaderFactory jwtReaderFactory;
    private final JwtWriterFactory jwtWriterFactory;

    public DefaultJwtService(JwtReaderFactory jwtReaderFactory, JwtWriterFactory jwtWriterFactory) {
        this.jwtReaderFactory = jwtReaderFactory;
        this.jwtWriterFactory = jwtWriterFactory;
    }

    @Override
    @Nonnull
    public String issueJwt(@Nonnull String jsonPayload, @Nonnull String sharedSecret) {
        return this.issueJwt(jsonPayload, sharedSecret, SigningAlgorithm.HS256);
    }

    @Override
    @Nonnull
    public String issueJwt(@Nonnull String jsonPayload, @Nonnull String sharedSecret, @Nonnull SigningAlgorithm algorithm) {
        return this.jwtWriterFactory.macSigningWriter(Objects.requireNonNull(algorithm, "algorithm"), Objects.requireNonNull(sharedSecret, "sharedSecret")).jsonToJwt(Objects.requireNonNull(jsonPayload, "jsonPayload"));
    }

    @Override
    @Nonnull
    public Jwt verifyJwt(@Nonnull String jwt, @Nonnull Map<String, ? extends JwtClaimVerifier> claimVerifiers) throws JwtIssuerLacksSharedSecretException, JwtParseException, JwtUnknownIssuerException, JwtVerificationException {
        return this.jwtReaderFactory.getReader(jwt).readAndVerify(jwt, claimVerifiers);
    }
}

