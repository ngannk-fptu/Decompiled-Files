/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.reader;

import com.atlassian.jwt.exception.JwsUnsupportedAlgorithmException;
import com.atlassian.jwt.exception.JwtIssuerLacksSharedSecretException;
import com.atlassian.jwt.exception.JwtParseException;
import com.atlassian.jwt.exception.JwtUnknownIssuerException;
import com.atlassian.jwt.reader.JwtReader;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import javax.annotation.Nonnull;

public interface JwtReaderFactory {
    @Nonnull
    public JwtReader getReader(@Nonnull String var1) throws JwsUnsupportedAlgorithmException, JwtUnknownIssuerException, JwtParseException, JwtIssuerLacksSharedSecretException;

    @Nonnull
    public JwtReader getReader(@Nonnull String var1, @Nonnull Date var2) throws JwsUnsupportedAlgorithmException, JwtUnknownIssuerException, JwtParseException, JwtIssuerLacksSharedSecretException;

    @Nonnull
    public JwtReader getReader(@Nonnull String var1, RSAPublicKey var2) throws JwsUnsupportedAlgorithmException, JwtUnknownIssuerException, JwtParseException;

    @Nonnull
    public JwtReader getReader(@Nonnull String var1, RSAPublicKey var2, @Nonnull Date var3) throws JwsUnsupportedAlgorithmException, JwtUnknownIssuerException, JwtParseException;
}

