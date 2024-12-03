/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.oauth2.client.api;

import java.time.Instant;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ClientToken {
    @Nonnull
    public String getAccessToken();

    @Nonnull
    public Instant getAccessTokenExpiration();

    @Nullable
    public String getRefreshToken();

    @Nullable
    public Instant getRefreshTokenExpiration();
}

