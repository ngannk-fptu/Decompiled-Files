/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.application.generic.GenericApplicationType
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.support;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.application.generic.GenericApplicationType;
import com.atlassian.applinks.application.BuiltinApplinksType;
import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.applinks.internal.status.support.ApplinkCompatibilityVerifier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DefaultApplinkCompatibilityVerifier
implements ApplinkCompatibilityVerifier {
    @Override
    @Nullable
    public ApplinkErrorType verifyLocalCompatibility(@Nonnull ApplicationLink applicationLink) {
        if (applicationLink.isSystem()) {
            return ApplinkErrorType.SYSTEM_LINK;
        }
        if (applicationLink.getType() instanceof GenericApplicationType) {
            return ApplinkErrorType.GENERIC_LINK;
        }
        if (!(applicationLink.getType() instanceof BuiltinApplinksType)) {
            return ApplinkErrorType.NON_ATLASSIAN;
        }
        return null;
    }
}

