/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.jwt;

import com.atlassian.jwt.reader.JwtClaimVerifiersBuilder;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface JwtIssuerClaimVerifiersRegistry {
    @Nullable
    public JwtClaimVerifiersBuilder getClaimVerifiersBuilder(@Nonnull String var1);
}

