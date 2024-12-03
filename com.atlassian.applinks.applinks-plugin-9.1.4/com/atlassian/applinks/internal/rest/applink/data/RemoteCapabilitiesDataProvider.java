/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.rest.applink.data;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.common.capabilities.ApplicationVersion;
import com.atlassian.applinks.internal.common.capabilities.RemoteCapabilitiesService;
import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.atlassian.applinks.internal.rest.applink.data.AbstractRestApplinkDataProvider;
import com.atlassian.applinks.internal.rest.model.capabilities.RestRemoteApplicationCapabilities;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

public class RemoteCapabilitiesDataProvider
extends AbstractRestApplinkDataProvider {
    public static final String CAPABILITIES = "capabilities";
    public static final String APPLICATION_VERSION = "applicationVersion";
    private final RemoteCapabilitiesService remoteCapabilitiesService;

    @Autowired
    public RemoteCapabilitiesDataProvider(RemoteCapabilitiesService remoteCapabilitiesService) {
        super((Set<String>)ImmutableSet.of((Object)CAPABILITIES, (Object)APPLICATION_VERSION));
        this.remoteCapabilitiesService = remoteCapabilitiesService;
    }

    @Override
    @Nullable
    public Object provide(@Nonnull String key, @Nonnull ApplicationLink applink) throws ServiceException {
        if (CAPABILITIES.equals(key)) {
            return new RestRemoteApplicationCapabilities(this.remoteCapabilitiesService.getCapabilities(applink));
        }
        if (APPLICATION_VERSION.equals(key)) {
            ApplicationVersion version = this.remoteCapabilitiesService.getCapabilities(applink).getApplicationVersion();
            return version != null ? version.getVersionString() : null;
        }
        throw new IllegalArgumentException(String.format("Unsupported key: '%s'", key));
    }
}

