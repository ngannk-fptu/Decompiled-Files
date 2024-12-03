/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 */
package com.atlassian.streams.internal.rest.resources.whitelist;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.streams.internal.rest.resources.whitelist.AppLinksUriSupplier;
import com.atlassian.streams.internal.rest.resources.whitelist.Whitelist;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.net.URI;

public class AppLinksWhitelist
implements Whitelist {
    private final ApplicationProperties applicationProperties;
    private final Supplier<Iterable<URI>> whitelist;

    public AppLinksWhitelist(ApplicationProperties applicationProperties, AppLinksUriSupplier whitelist) {
        this.applicationProperties = (ApplicationProperties)Preconditions.checkNotNull((Object)applicationProperties, (Object)"applicationProperties");
        this.whitelist = (Supplier)Preconditions.checkNotNull((Object)whitelist, (Object)"whitelist");
    }

    @Override
    public boolean allows(URI uri) {
        return Iterables.any((Iterable)Iterables.concat((Iterable)((Iterable)this.whitelist.get()), (Iterable)ImmutableList.of((Object)this.self())), this.prefixes((URI)Preconditions.checkNotNull((Object)uri, (Object)"uri"))) && this.notProxyResource().apply((Object)uri);
    }

    private Predicate<URI> prefixes(URI uri) {
        return new UriPrefixPredicate(uri);
    }

    private Predicate<URI> notProxyResource() {
        return new NotProxyResourcePredicate();
    }

    private URI self() {
        return URI.create(this.applicationProperties.getBaseUrl());
    }

    private final class NotProxyResourcePredicate
    implements Predicate<URI> {
        private NotProxyResourcePredicate() {
        }

        public boolean apply(URI uri) {
            return !uri.normalize().toASCIIString().toLowerCase().contains("url-proxy");
        }
    }

    private static final class UriPrefixPredicate
    implements Predicate<URI> {
        private final String uri;

        public UriPrefixPredicate(URI uri) {
            this.uri = uri.normalize().toASCIIString().toLowerCase();
        }

        public boolean apply(URI prefix) {
            return this.uri.startsWith(prefix.normalize().toASCIIString().toLowerCase());
        }
    }
}

