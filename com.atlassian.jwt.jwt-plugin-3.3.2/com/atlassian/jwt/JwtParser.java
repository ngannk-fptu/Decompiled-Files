/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt;

import com.atlassian.jwt.Jwt;
import com.atlassian.jwt.exception.JwtParseException;
import javax.annotation.Nonnull;

public interface JwtParser {
    @Nonnull
    public Jwt parse(String var1) throws JwtParseException;
}

