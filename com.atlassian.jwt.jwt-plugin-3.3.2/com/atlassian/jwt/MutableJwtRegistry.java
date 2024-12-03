/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.jwt;

import com.atlassian.jwt.JwtIssuer;
import com.atlassian.jwt.JwtIssuerRegistry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MutableJwtRegistry
extends JwtIssuerRegistry {
    @Nonnull
    public JwtIssuer addIssuer(@Nonnull String var1, @Nonnull String var2);

    public boolean removeIssuer(@Nonnull String var1);

    @Override
    @Nullable
    public JwtIssuer getIssuer(@Nonnull String var1);
}

