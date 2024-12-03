/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.common.capabilities;

import com.atlassian.annotations.Internal;
import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.common.capabilities.RemoteApplicationCapabilities;
import com.atlassian.applinks.internal.common.exception.InvalidArgumentException;
import com.atlassian.applinks.internal.common.exception.NoAccessException;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.applinks.internal.common.permission.PermissionLevel;
import com.atlassian.applinks.internal.common.permission.Restricted;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

@Restricted(value={PermissionLevel.ADMIN})
@Internal
public interface RemoteCapabilitiesService {
    @Nonnull
    public RemoteApplicationCapabilities getCapabilities(@Nonnull ApplicationId var1) throws NoSuchApplinkException, NoAccessException;

    @Nonnull
    public RemoteApplicationCapabilities getCapabilities(@Nonnull ApplicationLink var1) throws NoAccessException;

    @Nonnull
    public RemoteApplicationCapabilities getCapabilities(@Nonnull ApplicationId var1, long var2, @Nonnull TimeUnit var4) throws NoSuchApplinkException, NoAccessException, InvalidArgumentException;

    @Nonnull
    public RemoteApplicationCapabilities getCapabilities(@Nonnull ApplicationLink var1, long var2, @Nonnull TimeUnit var4) throws InvalidArgumentException, NoAccessException;
}

