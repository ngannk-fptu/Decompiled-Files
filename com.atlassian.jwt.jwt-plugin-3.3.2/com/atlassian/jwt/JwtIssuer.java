/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.jwt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface JwtIssuer {
    @Nonnull
    public String getName();

    @Nullable
    public String getSharedSecret();
}

