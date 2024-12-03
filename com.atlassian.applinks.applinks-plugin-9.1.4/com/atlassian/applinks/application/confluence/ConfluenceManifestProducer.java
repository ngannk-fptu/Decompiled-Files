/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.BasicAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TrustedAppsAuthenticationProvider
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.applinks.application.confluence;

import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.types.BasicAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TrustedAppsAuthenticationProvider;
import com.atlassian.applinks.application.confluence.ConfluenceApplicationTypeImpl;
import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.core.manifest.AppLinksManifestDownloader;
import com.atlassian.applinks.core.manifest.AppLinksManifestProducer;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.net.RequestFactory;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class ConfluenceManifestProducer
extends AppLinksManifestProducer {
    public ConfluenceManifestProducer(RequestFactory requestFactory, AppLinksManifestDownloader downloader, WebResourceManager webResourceManager, AppLinkPluginUtil appLinkPluginUtil) {
        super(requestFactory, downloader, webResourceManager, appLinkPluginUtil);
    }

    @Override
    protected TypeId getApplicationTypeId() {
        return ConfluenceApplicationTypeImpl.TYPE_ID;
    }

    @Override
    protected String getApplicationName() {
        return "Confluence";
    }

    @Override
    protected Set<Class<? extends AuthenticationProvider>> getSupportedInboundAuthenticationTypes() {
        return ImmutableSet.of(BasicAuthenticationProvider.class, TrustedAppsAuthenticationProvider.class, OAuthAuthenticationProvider.class);
    }

    @Override
    protected Set<Class<? extends AuthenticationProvider>> getSupportedOutboundAuthenticationTypes() {
        return ImmutableSet.of(BasicAuthenticationProvider.class, TrustedAppsAuthenticationProvider.class, OAuthAuthenticationProvider.class);
    }
}

