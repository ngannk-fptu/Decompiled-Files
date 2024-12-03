/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.BasicAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.applinks.application.generic;

import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.types.BasicAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.application.generic.GenericApplicationTypeImpl;
import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.core.manifest.AppLinksManifestDownloader;
import com.atlassian.applinks.core.manifest.AppLinksManifestProducer;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.net.RequestFactory;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class GenericManifestProducer
extends AppLinksManifestProducer {
    public GenericManifestProducer(RequestFactory requestFactory, AppLinksManifestDownloader downloader, WebResourceManager webResourceManager, AppLinkPluginUtil AppLinkPluginUtil2) {
        super(requestFactory, downloader, webResourceManager, AppLinkPluginUtil2);
    }

    @Override
    protected TypeId getApplicationTypeId() {
        return GenericApplicationTypeImpl.TYPE_ID;
    }

    @Override
    protected String getApplicationName() {
        return "Generic Application";
    }

    @Override
    protected Set<Class<? extends AuthenticationProvider>> getSupportedInboundAuthenticationTypes() {
        return ImmutableSet.of(BasicAuthenticationProvider.class, OAuthAuthenticationProvider.class);
    }

    @Override
    protected Set<Class<? extends AuthenticationProvider>> getSupportedOutboundAuthenticationTypes() {
        return ImmutableSet.of(BasicAuthenticationProvider.class, OAuthAuthenticationProvider.class);
    }
}

