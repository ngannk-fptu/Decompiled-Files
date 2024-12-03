/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.writer;

import javax.annotation.Nonnull;

public interface JwtJsonBuilder {
    @Nonnull
    public JwtJsonBuilder audience(@Nonnull String var1);

    @Nonnull
    public JwtJsonBuilder expirationTime(long var1);

    public boolean isClaimSet(@Nonnull String var1);

    @Nonnull
    public JwtJsonBuilder issuedAt(long var1);

    @Nonnull
    public JwtJsonBuilder issuer(@Nonnull String var1);

    @Nonnull
    public JwtJsonBuilder jwtId(@Nonnull String var1);

    @Nonnull
    public JwtJsonBuilder notBefore(long var1);

    @Nonnull
    public JwtJsonBuilder subject(@Nonnull String var1);

    @Nonnull
    public JwtJsonBuilder type(@Nonnull String var1);

    @Nonnull
    public JwtJsonBuilder queryHash(@Nonnull String var1);

    @Nonnull
    public JwtJsonBuilder claim(@Nonnull String var1, @Nonnull Object var2);

    @Nonnull
    public String build();
}

