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

public interface ClientTokenMetadata {
    @Nonnull
    public ClientTokenStatus getStatus();

    @Nonnull
    public Instant getLastStatusUpdated();

    @Nullable
    public Instant getLastRefreshed();

    public int getRefreshCount();

    public static enum ClientTokenStatus {
        UNKNOWN,
        VALID,
        RECOVERABLE,
        UNRECOVERABLE;

    }
}

