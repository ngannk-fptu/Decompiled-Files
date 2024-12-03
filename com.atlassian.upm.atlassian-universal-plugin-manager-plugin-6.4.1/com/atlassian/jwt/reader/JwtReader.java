/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.reader;

import com.atlassian.jwt.Jwt;
import com.atlassian.jwt.exception.JwtParseException;
import com.atlassian.jwt.exception.JwtVerificationException;
import com.atlassian.jwt.reader.JwtClaimVerifier;
import java.util.Map;
import javax.annotation.Nonnull;

public interface JwtReader {
    @Nonnull
    public Jwt readUnverified(@Nonnull String var1) throws JwtParseException, JwtVerificationException;

    @Nonnull
    public Jwt readAndVerify(@Nonnull String var1, @Nonnull Map<String, ? extends JwtClaimVerifier> var2) throws JwtParseException, JwtVerificationException;

    @Deprecated
    @Nonnull
    public Jwt read(@Nonnull String var1, @Nonnull Map<String, ? extends JwtClaimVerifier> var2) throws JwtParseException, JwtVerificationException;
}

