/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.navlink.consumer.menu.services;

import com.atlassian.plugins.navlink.producer.navigation.NavigationLink;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLinkBuilder;
import java.net.URI;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MaskBitbucketNavigationLinkMapper
implements Function<NavigationLink, NavigationLink> {
    public static final MaskBitbucketNavigationLinkMapper INSTANCE = new MaskBitbucketNavigationLinkMapper();
    private static final String BITBUCKET = "bitbucket";

    @Override
    public NavigationLink apply(@Nullable NavigationLink menuNavigationLink) {
        return menuNavigationLink != null ? this.transformLink(menuNavigationLink) : null;
    }

    @Nonnull
    private NavigationLink transformLink(@Nonnull NavigationLink menuNavigationLink) {
        String authority = this.getAuthority(menuNavigationLink);
        if (authority.toLowerCase().contains("bitbucket.org")) {
            return ((NavigationLinkBuilder)NavigationLinkBuilder.copyOf(menuNavigationLink).applicationType(BITBUCKET)).build();
        }
        return menuNavigationLink;
    }

    @Nonnull
    private String getAuthority(@Nonnull NavigationLink menuNavigationLink) {
        URI uri = this.asUri(menuNavigationLink);
        String authority = uri != null ? uri.getAuthority() : null;
        return authority != null ? authority : "";
    }

    @Nullable
    private URI asUri(@Nonnull NavigationLink menuNavigationLink) {
        try {
            return URI.create(menuNavigationLink.getHref());
        }
        catch (RuntimeException e) {
            return null;
        }
    }
}

