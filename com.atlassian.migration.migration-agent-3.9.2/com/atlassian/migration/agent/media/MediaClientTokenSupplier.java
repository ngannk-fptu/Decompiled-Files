/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.migration.agent.media;

import com.atlassian.migration.agent.media.MediaClientToken;
import javax.annotation.Nonnull;

public interface MediaClientTokenSupplier {
    @Nonnull
    public MediaClientToken getToken(String var1);

    @Nonnull
    public MediaClientToken getRefreshedToken(String var1);
}

