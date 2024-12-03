/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.jwt.applinks;

import com.atlassian.applinks.api.ApplicationLink;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface JwtApplinkFinder {
    @Nullable
    public ApplicationLink find(@Nonnull String var1);
}

