/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.jwt;

import com.atlassian.jwt.JwtIssuer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface JwtIssuerRegistry {
    @Nullable
    public JwtIssuer getIssuer(@Nonnull String var1);
}

