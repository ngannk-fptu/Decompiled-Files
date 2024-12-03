/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.core;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.core.ApplinkStatus;
import com.atlassian.applinks.internal.common.exception.NoAccessException;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.applinks.internal.common.permission.PermissionLevel;
import com.atlassian.applinks.internal.common.permission.Restricted;
import javax.annotation.Nonnull;

@Restricted(value={PermissionLevel.ADMIN})
public interface ApplinkStatusService {
    @Nonnull
    public ApplinkStatus getApplinkStatus(@Nonnull ApplicationId var1) throws NoAccessException, NoSuchApplinkException;
}

