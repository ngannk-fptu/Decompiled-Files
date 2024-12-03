/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.oauth.consumer.ConsumerTokenStore
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.inject.AbstractModule
 *  com.google.inject.Module
 *  com.google.inject.name.Names
 *  org.apache.shindig.common.crypto.BlobCrypter
 *  org.apache.shindig.gadgets.oauth.OAuthCallbackGenerator
 *  org.apache.shindig.gadgets.oauth.OAuthFetcherFactory
 *  org.apache.shindig.gadgets.oauth.OAuthModule$OAuthCrypterProvider
 *  org.apache.shindig.gadgets.oauth.OAuthStore
 *  org.apache.shindig.gadgets.servlet.AuthenticationModule
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.renderer.internal.guice;

import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.gadgets.renderer.internal.AtlassianOAuthCallbackGenerator;
import com.atlassian.gadgets.renderer.internal.oauth.AtlassianOAuthFetcherFactory;
import com.atlassian.gadgets.renderer.internal.oauth.AtlassianOAuthStore;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.oauth.consumer.ConsumerTokenStore;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.name.Names;
import java.lang.annotation.Annotation;
import org.apache.shindig.common.crypto.BlobCrypter;
import org.apache.shindig.gadgets.oauth.OAuthCallbackGenerator;
import org.apache.shindig.gadgets.oauth.OAuthFetcherFactory;
import org.apache.shindig.gadgets.oauth.OAuthModule;
import org.apache.shindig.gadgets.oauth.OAuthStore;
import org.apache.shindig.gadgets.servlet.AuthenticationModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthModule
extends AbstractModule {
    private final ConsumerService consumerService;
    private final ConsumerTokenStore tokenStore;
    private final ReadOnlyApplicationLinkService readOnlyApplicationLinkService;

    @Autowired
    public AuthModule(@ComponentImport ConsumerService consumerService, @ComponentImport ConsumerTokenStore tokenStore, @ComponentImport ReadOnlyApplicationLinkService readOnlyApplicationLinkService) {
        this.consumerService = consumerService;
        this.tokenStore = tokenStore;
        this.readOnlyApplicationLinkService = readOnlyApplicationLinkService;
    }

    protected void configure() {
        this.bind(Boolean.class).annotatedWith((Annotation)Names.named((String)"shindig.allowUnauthenticated")).toInstance((Object)Boolean.TRUE);
        this.install((Module)new AuthenticationModule());
        this.bind(BlobCrypter.class).annotatedWith((Annotation)Names.named((String)"shindig.oauth.state-crypter")).toProvider(OAuthModule.OAuthCrypterProvider.class);
        this.bind(OAuthStore.class).to(AtlassianOAuthStore.class);
        this.bind(OAuthFetcherFactory.class).to(AtlassianOAuthFetcherFactory.class);
        this.bind(ConsumerService.class).toInstance((Object)this.consumerService);
        this.bind(ConsumerTokenStore.class).toInstance((Object)this.tokenStore);
        this.bind(ReadOnlyApplicationLinkService.class).toInstance((Object)this.readOnlyApplicationLinkService);
        this.bind(OAuthCallbackGenerator.class).to(AtlassianOAuthCallbackGenerator.class);
    }
}

