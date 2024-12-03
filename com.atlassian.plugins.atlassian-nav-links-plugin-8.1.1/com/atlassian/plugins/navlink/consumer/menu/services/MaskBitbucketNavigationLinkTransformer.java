/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Strings
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.navlink.consumer.menu.services;

import com.atlassian.plugins.navlink.producer.navigation.NavigationLink;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLinkBuilder;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import java.net.URI;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Deprecated
public class MaskBitbucketNavigationLinkTransformer
implements Function<NavigationLink, NavigationLink> {
    public static final MaskBitbucketNavigationLinkTransformer INSTANCE = new MaskBitbucketNavigationLinkTransformer();
    private static final String BITBUCKET = "bitbucket";

    public NavigationLink apply(@Nullable NavigationLink menuNavigationLink) {
        return menuNavigationLink != null ? this.transformLink(menuNavigationLink) : null;
    }

    @Nonnull
    private NavigationLink transformLink(@Nonnull NavigationLink menuNavigationLink) {
        String authority = Strings.nullToEmpty((String)this.getAuthority(menuNavigationLink));
        if (authority.toLowerCase().contains("bitbucket.org")) {
            return ((NavigationLinkBuilder)NavigationLinkBuilder.copyOf(menuNavigationLink).applicationType(BITBUCKET)).build();
        }
        return menuNavigationLink;
    }

    @Nullable
    private String getAuthority(@Nonnull NavigationLink menuNavigationLink) {
        URI uri = this.asUri(menuNavigationLink);
        return uri != null ? uri.getAuthority() : null;
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

