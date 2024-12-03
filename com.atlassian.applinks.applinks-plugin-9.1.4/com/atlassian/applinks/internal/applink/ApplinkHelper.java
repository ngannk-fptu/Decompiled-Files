/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.spi.link.MutableApplicationLink
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.applink;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.applinks.internal.common.permission.Unrestricted;
import com.atlassian.applinks.spi.link.MutableApplicationLink;
import javax.annotation.Nonnull;

@Unrestricted(value="Clients using this component are responsible for enforcing appropriate permissions")
public interface ApplinkHelper {
    @Nonnull
    public ApplicationLink getApplicationLink(@Nonnull ApplicationId var1) throws NoSuchApplinkException;

    @Nonnull
    public MutableApplicationLink getMutableApplicationLink(@Nonnull ApplicationId var1) throws NoSuchApplinkException;

    @Nonnull
    public ReadOnlyApplicationLink getReadOnlyApplicationLink(@Nonnull ApplicationId var1) throws NoSuchApplinkException;

    public void makePrimary(@Nonnull ApplicationId var1) throws NoSuchApplinkException;
}

