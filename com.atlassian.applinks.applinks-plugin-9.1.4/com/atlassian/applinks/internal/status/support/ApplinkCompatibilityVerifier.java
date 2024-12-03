/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.support;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ApplinkCompatibilityVerifier {
    @Nullable
    public ApplinkErrorType verifyLocalCompatibility(@Nonnull ApplicationLink var1);
}

