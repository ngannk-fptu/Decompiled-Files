/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.rememberme;

import java.time.LocalDateTime;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface CrowdRememberMeToken {
    public Long getId();

    @Nonnull
    public String getToken();

    @Nonnull
    public String getSeries();

    @Nonnull
    public String getUsername();

    @Nonnull
    public LocalDateTime getCreatedTime();

    public LocalDateTime getUsedTime();

    @Nonnull
    public Long getDirectoryId();

    @Nullable
    public String getRemoteAddress();
}

