/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.atlassian.streams.internal.rest.resources.whitelist;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import java.net.URI;

class AppLinksUriSupplier
implements Supplier<Iterable<URI>> {
    private final ApplicationLinkService appLinkService;

    public AppLinksUriSupplier(ApplicationLinkService appLinkService) {
        this.appLinkService = (ApplicationLinkService)Preconditions.checkNotNull((Object)appLinkService, (Object)"appLinkService");
    }

    public Iterable<URI> get() {
        ImmutableList.Builder uris = ImmutableList.builder();
        for (ApplicationLink appLink : this.appLinkService.getApplicationLinks()) {
            uris.add((Object)appLink.getRpcUrl());
        }
        return uris.build();
    }
}

