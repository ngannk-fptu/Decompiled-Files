/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthAuthenticationProvider
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.internal.common.auth.oauth.ServiceProviderStoreService
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.sal.api.net.RequestFactory
 */
package com.atlassian.applinks.oauth.auth.twolo;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthAuthenticationProvider;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.auth.oauth.ServiceProviderStoreService;
import com.atlassian.applinks.oauth.auth.twolo.AbstractTwoLeggedOAuthAuthenticatorProviderPluginModule;
import com.atlassian.applinks.oauth.auth.twolo.TwoLeggedOAuthRequestFactoryImpl;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.sal.api.net.RequestFactory;

public class TwoLeggedOAuthAuthenticatorProviderPluginModule
extends AbstractTwoLeggedOAuthAuthenticatorProviderPluginModule {
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private final ConsumerService consumerService;
    private final RequestFactory requestFactory;
    private final ServiceProviderStoreService serviceProviderStoreService;

    public TwoLeggedOAuthAuthenticatorProviderPluginModule(AuthenticationConfigurationManager authenticationConfigurationManager, ConsumerService consumerService, InternalHostApplication hostApplication, RequestFactory requestFactory, ServiceProviderStoreService serviceProviderStoreService) {
        super(hostApplication);
        this.authenticationConfigurationManager = authenticationConfigurationManager;
        this.consumerService = consumerService;
        this.requestFactory = requestFactory;
        this.serviceProviderStoreService = serviceProviderStoreService;
    }

    public AuthenticationProvider getAuthenticationProvider(final ApplicationLink link) {
        TwoLeggedOAuthAuthenticationProvider provider = null;
        if (this.authenticationConfigurationManager.isConfigured(link.getId(), TwoLeggedOAuthAuthenticationProvider.class)) {
            provider = new TwoLeggedOAuthAuthenticationProvider(){

                public ApplicationLinkRequestFactory getRequestFactory() {
                    return new TwoLeggedOAuthRequestFactoryImpl(link, TwoLeggedOAuthAuthenticatorProviderPluginModule.this.authenticationConfigurationManager, TwoLeggedOAuthAuthenticatorProviderPluginModule.this.consumerService, TwoLeggedOAuthAuthenticatorProviderPluginModule.this.requestFactory);
                }
            };
        }
        return provider;
    }

    public boolean incomingEnabled(ApplicationLink applicationLink) {
        Consumer consumer = this.serviceProviderStoreService.getConsumer(applicationLink);
        return consumer != null && consumer.getTwoLOAllowed();
    }

    @Override
    public Class<? extends AuthenticationProvider> getAuthenticationProviderClass() {
        return TwoLeggedOAuthAuthenticationProvider.class;
    }
}

