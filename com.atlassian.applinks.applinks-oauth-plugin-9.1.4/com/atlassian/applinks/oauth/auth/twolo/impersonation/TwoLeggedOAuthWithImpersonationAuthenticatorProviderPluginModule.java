/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider
 *  com.atlassian.applinks.core.ElevatedPermissionsService
 *  com.atlassian.applinks.core.auth.OrphanedTrustAwareAuthenticatorProviderPluginModule
 *  com.atlassian.applinks.core.auth.OrphanedTrustCertificate$Type
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.internal.common.auth.oauth.OAuthAutoConfigurator
 *  com.atlassian.applinks.internal.common.auth.oauth.ServiceProviderStoreService
 *  com.atlassian.applinks.internal.common.permission.PermissionLevel
 *  com.atlassian.applinks.internal.common.status.oauth.OAuthConfig
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationException
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.spi.auth.AuthenticationScenario
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.google.common.base.Throwables
 */
package com.atlassian.applinks.oauth.auth.twolo.impersonation;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider;
import com.atlassian.applinks.core.ElevatedPermissionsService;
import com.atlassian.applinks.core.auth.OrphanedTrustAwareAuthenticatorProviderPluginModule;
import com.atlassian.applinks.core.auth.OrphanedTrustCertificate;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.auth.oauth.OAuthAutoConfigurator;
import com.atlassian.applinks.internal.common.auth.oauth.ServiceProviderStoreService;
import com.atlassian.applinks.internal.common.permission.PermissionLevel;
import com.atlassian.applinks.internal.common.status.oauth.OAuthConfig;
import com.atlassian.applinks.oauth.auth.twolo.AbstractTwoLeggedOAuthAuthenticatorProviderPluginModule;
import com.atlassian.applinks.oauth.auth.twolo.impersonation.TwoLeggedOAuthWithImpersonationRequestFactoryImpl;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationException;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.spi.auth.AuthenticationScenario;
import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.sal.api.net.RequestFactory;
import com.google.common.base.Throwables;
import java.util.concurrent.Callable;

public class TwoLeggedOAuthWithImpersonationAuthenticatorProviderPluginModule
extends AbstractTwoLeggedOAuthAuthenticatorProviderPluginModule
implements OrphanedTrustAwareAuthenticatorProviderPluginModule {
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private final ConsumerService consumerService;
    private final OAuthAutoConfigurator oAuthAutoConfigurator;
    private final RequestFactory requestFactory;
    private final ServiceProviderStoreService serviceProviderStoreService;
    private final ElevatedPermissionsService elevatedPermissions;

    public TwoLeggedOAuthWithImpersonationAuthenticatorProviderPluginModule(AuthenticationConfigurationManager authenticationConfigurationManager, ConsumerService consumerService, OAuthAutoConfigurator oAuthAutoConfigurator, InternalHostApplication hostApplication, RequestFactory requestFactory, ServiceProviderStoreService serviceProviderStoreService, ElevatedPermissionsService elevatedPermissions) {
        super(hostApplication);
        this.authenticationConfigurationManager = authenticationConfigurationManager;
        this.consumerService = consumerService;
        this.requestFactory = requestFactory;
        this.oAuthAutoConfigurator = oAuthAutoConfigurator;
        this.serviceProviderStoreService = serviceProviderStoreService;
        this.elevatedPermissions = elevatedPermissions;
    }

    public AuthenticationProvider getAuthenticationProvider(final ApplicationLink link) {
        TwoLeggedOAuthWithImpersonationAuthenticationProvider provider = null;
        if (this.authenticationConfigurationManager.isConfigured(link.getId(), TwoLeggedOAuthWithImpersonationAuthenticationProvider.class)) {
            provider = new TwoLeggedOAuthWithImpersonationAuthenticationProvider(){

                public ApplicationLinkRequestFactory getRequestFactory(String username) {
                    return new TwoLeggedOAuthWithImpersonationRequestFactoryImpl(link, TwoLeggedOAuthWithImpersonationAuthenticatorProviderPluginModule.this.authenticationConfigurationManager, TwoLeggedOAuthWithImpersonationAuthenticatorProviderPluginModule.this.consumerService, TwoLeggedOAuthWithImpersonationAuthenticatorProviderPluginModule.this.requestFactory, username);
                }
            };
        }
        return provider;
    }

    public boolean isApplicable(AuthenticationScenario authenticationScenario, ApplicationLink applicationLink) {
        return authenticationScenario.isCommonUserBase() && authenticationScenario.isTrusted();
    }

    public boolean isApplicable(String certificateType) {
        return OrphanedTrustCertificate.Type.OAUTH.name().equals(certificateType);
    }

    public boolean incomingEnabled(ApplicationLink applicationLink) {
        Consumer consumer = this.serviceProviderStoreService.getConsumer(applicationLink);
        return consumer != null && consumer.getTwoLOAllowed() && consumer.getTwoLOImpersonationAllowed();
    }

    public void enable(final RequestFactory authenticatedRequestFactory, final ApplicationLink applink) throws AuthenticationConfigurationException {
        try {
            this.elevatedPermissions.executeAs(PermissionLevel.SYSADMIN, (Callable)new Callable<Void>(){

                @Override
                public Void call() throws Exception {
                    TwoLeggedOAuthWithImpersonationAuthenticatorProviderPluginModule.this.oAuthAutoConfigurator.enable(OAuthConfig.createOAuthWithImpersonationConfig(), applink, authenticatedRequestFactory);
                    return null;
                }
            });
        }
        catch (Exception e) {
            Throwables.propagateIfInstanceOf((Throwable)e, AuthenticationConfigurationException.class);
            throw new AuthenticationConfigurationException((Throwable)e);
        }
    }

    public void disable(RequestFactory authenticatedRequestFactory, ApplicationLink applicationLink) throws AuthenticationConfigurationException {
    }

    @Override
    public Class<? extends AuthenticationProvider> getAuthenticationProviderClass() {
        return TwoLeggedOAuthWithImpersonationAuthenticationProvider.class;
    }
}

