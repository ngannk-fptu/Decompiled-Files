/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.inject.Inject
 *  com.google.inject.Singleton
 *  org.apache.shindig.gadgets.GadgetException
 *  org.apache.shindig.gadgets.http.HttpFetcher
 *  org.apache.shindig.gadgets.http.HttpRequest
 *  org.apache.shindig.gadgets.oauth.OAuthFetcher
 *  org.apache.shindig.gadgets.oauth.OAuthFetcherConfig
 *  org.apache.shindig.gadgets.oauth.OAuthFetcherFactory
 */
package com.atlassian.gadgets.renderer.internal.oauth;

import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.gadgets.renderer.internal.oauth.AtlassianOAuthFetcher;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.http.HttpFetcher;
import org.apache.shindig.gadgets.http.HttpRequest;
import org.apache.shindig.gadgets.oauth.OAuthFetcher;
import org.apache.shindig.gadgets.oauth.OAuthFetcherConfig;
import org.apache.shindig.gadgets.oauth.OAuthFetcherFactory;

@Singleton
public class AtlassianOAuthFetcherFactory
extends OAuthFetcherFactory {
    private final ConsumerService consumerService;
    private final ReadOnlyApplicationLinkService applicationLinkService;

    @Inject
    protected AtlassianOAuthFetcherFactory(OAuthFetcherConfig fetcherConfig, @ComponentImport ConsumerService consumerService, @ComponentImport ReadOnlyApplicationLinkService applicationLinkService) {
        super(fetcherConfig);
        this.consumerService = consumerService;
        this.applicationLinkService = applicationLinkService;
    }

    public OAuthFetcher getOAuthFetcher(HttpFetcher nextFetcher, HttpRequest request) throws GadgetException {
        return new AtlassianOAuthFetcher(this.consumerService, this.applicationLinkService, this.fetcherConfig, nextFetcher, request);
    }
}

