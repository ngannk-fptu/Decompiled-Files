/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.failurecache.CacheLoader
 *  com.atlassian.failurecache.ExpiringValue
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.util.concurrent.Futures
 *  com.google.common.util.concurrent.ListenableFuture
 */
package com.atlassian.plugins.navlink.consumer.menu.services;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.failurecache.CacheLoader;
import com.atlassian.failurecache.ExpiringValue;
import com.atlassian.plugins.navlink.consumer.menu.client.capabilities.CapabilitiesClient;
import com.atlassian.plugins.navlink.consumer.menu.services.IgnoreRemotePluginNavigationPredicate;
import com.atlassian.plugins.navlink.producer.capabilities.RemoteApplicationWithCapabilities;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CapabilitiesCacheLoader
implements CacheLoader<ApplicationId, RemoteApplicationWithCapabilities> {
    private final ReadOnlyApplicationLinkService readOnlyApplicationLinkService;
    private final ApplicationLinkService applicationLinkService;
    private final CapabilitiesClient capabilitiesClient;

    public CapabilitiesCacheLoader(ReadOnlyApplicationLinkService readOnlyApplicationLinkService, ApplicationLinkService applicationLinkService, CapabilitiesClient capabilitiesClient) {
        this.readOnlyApplicationLinkService = readOnlyApplicationLinkService;
        this.applicationLinkService = applicationLinkService;
        this.capabilitiesClient = capabilitiesClient;
    }

    public ImmutableSet<ApplicationId> getKeys() {
        Set results = StreamSupport.stream(this.readOnlyApplicationLinkService.getApplicationLinks().spliterator(), false).filter(new IgnoreRemotePluginNavigationPredicate(this.applicationLinkService).negate()).map(this.toApplicationId()).collect(Collectors.toSet());
        return ImmutableSet.copyOf(results);
    }

    public ListenableFuture<ExpiringValue<RemoteApplicationWithCapabilities>> loadValue(ApplicationId applicationId) {
        try {
            ReadOnlyApplicationLink applicationLink = this.getApplicationLink(applicationId);
            return this.capabilitiesClient.getCapabilities(applicationLink);
        }
        catch (RuntimeException e) {
            return Futures.immediateFailedFuture((Throwable)e);
        }
        catch (TypeNotInstalledException e) {
            return Futures.immediateFailedFuture((Throwable)e);
        }
    }

    private ReadOnlyApplicationLink getApplicationLink(ApplicationId applicationId) throws TypeNotInstalledException {
        return Objects.requireNonNull(this.readOnlyApplicationLinkService.getApplicationLink(applicationId), "Application link with application id '" + applicationId + "' is not existing.");
    }

    private Function<ReadOnlyApplicationLink, ApplicationId> toApplicationId() {
        return from -> from != null ? from.getId() : null;
    }
}

