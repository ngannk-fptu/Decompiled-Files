/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.core.reader;

import com.atlassian.jwt.exception.JwtIssuerLacksSharedSecretException;
import com.atlassian.jwt.exception.JwtUnknownIssuerException;
import javax.annotation.Nonnull;

public interface JwtIssuerSharedSecretService {
    public String getSharedSecret(@Nonnull String var1) throws JwtIssuerLacksSharedSecretException, JwtUnknownIssuerException;
}

