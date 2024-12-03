/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.navlink.producer.navigation;

import com.atlassian.plugins.custom_apps.api.CustomApp;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLinkBuilder;
import com.atlassian.plugins.navlink.producer.navigation.links.LinkSource;
import com.atlassian.plugins.navlink.producer.navigation.links.SourceType;
import com.atlassian.plugins.navlink.producer.navigation.services.RawNavigationLink;
import org.apache.commons.lang3.StringUtils;

public final class NavigationLinks {
    private NavigationLinks() {
        throw new AssertionError((Object)"Don't instantiate me");
    }

    public static NavigationLinkBuilder copyOf(CustomApp customApp) {
        return (NavigationLinkBuilder)((NavigationLinkBuilder)((NavigationLinkBuilder)((NavigationLinkBuilder)new NavigationLinkBuilder().source(NavigationLinks.retrieveSource(customApp))).href(customApp.getUrl())).label(customApp.getDisplayName()).applicationType(customApp.getSourceApplicationType())).self(customApp.isSelf());
    }

    public static NavigationLinkBuilder copyOf(RawNavigationLink navigationLink) {
        NavigationLinkBuilder builder = (NavigationLinkBuilder)new NavigationLinkBuilder().copy(navigationLink);
        if (navigationLink.getSource().type() == SourceType.UNKNOWN) {
            builder.source(LinkSource.localDefault());
        }
        return builder;
    }

    private static LinkSource retrieveSource(CustomApp customApp) {
        return NavigationLinks.isLocalCustomApp(customApp) ? LinkSource.localDefault() : LinkSource.remoteDefault();
    }

    private static boolean isLocalCustomApp(CustomApp ca) {
        return StringUtils.isBlank((CharSequence)ca.getSourceApplicationUrl());
    }
}

