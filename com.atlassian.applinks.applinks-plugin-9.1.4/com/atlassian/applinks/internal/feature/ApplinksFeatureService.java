/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.feature;

import com.atlassian.applinks.internal.common.exception.NoAccessException;
import com.atlassian.applinks.internal.common.exception.SystemFeatureException;
import com.atlassian.applinks.internal.common.permission.PermissionLevel;
import com.atlassian.applinks.internal.common.permission.Restricted;
import com.atlassian.applinks.internal.common.permission.Unrestricted;
import com.atlassian.applinks.internal.feature.ApplinksFeatures;
import javax.annotation.Nonnull;

public interface ApplinksFeatureService {
    @Unrestricted(value="Anonymous access allowed for convenience of usage, however it will not produce any meaningful result")
    public boolean isEnabled(@Nonnull ApplinksFeatures var1);

    @Restricted(value={PermissionLevel.SYSADMIN})
    public void enable(@Nonnull ApplinksFeatures var1, ApplinksFeatures ... var2) throws NoAccessException, SystemFeatureException;

    @Restricted(value={PermissionLevel.SYSADMIN})
    public void disable(@Nonnull ApplinksFeatures var1, ApplinksFeatures ... var2) throws NoAccessException, SystemFeatureException;
}

