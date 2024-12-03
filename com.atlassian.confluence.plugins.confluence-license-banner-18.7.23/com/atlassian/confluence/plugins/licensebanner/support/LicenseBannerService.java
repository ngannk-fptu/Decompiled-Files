/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.licensebanner.support;

import com.atlassian.confluence.plugins.licensebanner.support.LicenseDetails;
import com.atlassian.sal.api.user.UserKey;
import javax.annotation.Nonnull;

public interface LicenseBannerService {
    @Nonnull
    public LicenseDetails retrieveLicenseDetails(UserKey var1);

    public void remindNever(UserKey var1);

    public void remindLater(UserKey var1);
}

