/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.writer;

import com.atlassian.jwt.exception.JwtSigningException;
import javax.annotation.Nonnull;

public interface JwtWriter {
    @Nonnull
    public String jsonToJwt(@Nonnull String var1) throws JwtSigningException;
}

