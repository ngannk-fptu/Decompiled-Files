/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt;

import com.atlassian.jwt.Jwt;
import com.atlassian.jwt.SigningAlgorithm;
import com.atlassian.jwt.exception.JwtIssuerLacksSharedSecretException;
import com.atlassian.jwt.exception.JwtParseException;
import com.atlassian.jwt.exception.JwtUnknownIssuerException;
import com.atlassian.jwt.exception.JwtVerificationException;
import com.atlassian.jwt.reader.JwtClaimVerifier;
import java.util.Map;
import javax.annotation.Nonnull;

public interface JwtService {
    @Nonnull
    public String issueJwt(@Nonnull String var1, @Nonnull String var2);

    @Nonnull
    public String issueJwt(@Nonnull String var1, @Nonnull String var2, SigningAlgorithm var3);

    @Nonnull
    public Jwt verifyJwt(@Nonnull String var1, @Nonnull Map<String, ? extends JwtClaimVerifier> var2) throws JwtIssuerLacksSharedSecretException, JwtParseException, JwtUnknownIssuerException, JwtVerificationException;
}

