/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.core.ElevatedPermissionsService
 *  com.atlassian.applinks.core.ServletPathConstants
 *  com.atlassian.applinks.core.auth.OrphanedTrustAwareAuthenticatorProviderPluginModule
 *  com.atlassian.applinks.core.auth.OrphanedTrustCertificate$Type
 *  com.atlassian.applinks.core.util.RequestUtil
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService
 *  com.atlassian.applinks.internal.common.auth.oauth.OAuthAutoConfigurator
 *  com.atlassian.applinks.internal.common.auth.oauth.ServiceProviderStoreService
 *  com.atlassian.applinks.internal.common.permission.PermissionLevel
 *  com.atlassian.applinks.internal.common.status.oauth.OAuthConfig
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationException
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.spi.auth.AuthenticationDirection
 *  com.atlassian.applinks.spi.auth.AuthenticationScenario
 *  com.atlassian.applinks.spi.auth.IncomingTrustAuthenticationProviderPluginModule
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.base.Throwables
 *  javax.servlet.http.HttpServletRequest
 *  org.osgi.framework.Version
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.oauth.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.core.ElevatedPermissionsService;
import com.atlassian.applinks.core.ServletPathConstants;
import com.atlassian.applinks.core.auth.OrphanedTrustAwareAuthenticatorProviderPluginModule;
import com.atlassian.applinks.core.auth.OrphanedTrustCertificate;
import com.atlassian.applinks.core.util.RequestUtil;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService;
import com.atlassian.applinks.internal.common.auth.oauth.OAuthAutoConfigurator;
import com.atlassian.applinks.internal.common.auth.oauth.ServiceProviderStoreService;
import com.atlassian.applinks.internal.common.permission.PermissionLevel;
import com.atlassian.applinks.internal.common.status.oauth.OAuthConfig;
import com.atlassian.applinks.oauth.auth.OAuthHelper;
import com.atlassian.applinks.oauth.auth.ThreeLeggedOAuthRequestFactoryImpl;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationException;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.spi.auth.AuthenticationDirection;
import com.atlassian.applinks.spi.auth.AuthenticationScenario;
import com.atlassian.applinks.spi.auth.IncomingTrustAuthenticationProviderPluginModule;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.base.Throwables;
import java.net.URI;
import java.util.concurrent.Callable;
import javax.servlet.http.HttpServletRequest;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthAuthenticatorProviderPluginModule
implements OrphanedTrustAwareAuthenticatorProviderPluginModule,
IncomingTrustAuthenticationProviderPluginModule {
    public static final String ADD_CONSUMER_MANUALLY_SERVLET_LOCATION = ServletPathConstants.APPLINKS_CONFIG_SERVLET_PATH + "/oauth/add-consumer-manually/";
    public static final String ADD_CONSUMER_BY_URL_SERVLET_LOCATION = ServletPathConstants.APPLINKS_CONFIG_SERVLET_PATH + "/oauth/add-consumer-by-url/";
    public static final String OUTBOUND_NON_APPLINKS_SERVLET_LOCATION = ServletPathConstants.APPLINKS_CONFIG_SERVLET_PATH + "/oauth/outbound/3rdparty/";
    public static final String OUTBOUND_ATLASSIAN_SERVLET_LOCATION = ServletPathConstants.APPLINKS_CONFIG_SERVLET_PATH + "/oauth/outbound/atlassian/";
    public static final String OUTBOUND_ATLASSIAN_REDIRECT_LOCATION = ServletPathConstants.APPLINKS_CONFIG_SERVLET_PATH + "/oauth/outbound/apl-redirect/";
    private static final Logger log = LoggerFactory.getLogger(OAuthAuthenticatorProviderPluginModule.class);
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private final ConsumerService consumerService;
    private final ConsumerTokenStoreService consumerTokenStoreService;
    private final InternalHostApplication hostApplication;
    private final OAuthAutoConfigurator oAuthAutoConfigurator;
    private final RequestFactory requestFactory;
    private final UserManager userManager;
    private final ServiceProviderStoreService serviceProviderStoreService;
    private final ElevatedPermissionsService elevatedPermissions;

    public OAuthAuthenticatorProviderPluginModule(AuthenticationConfigurationManager authenticationConfigurationManager, ConsumerService consumerService, ConsumerTokenStoreService consumerTokenStoreService, InternalHostApplication hostApplication, OAuthAutoConfigurator oAuthAutoConfigurator, RequestFactory requestFactory, UserManager userManager, ServiceProviderStoreService serviceProviderStoreService, ElevatedPermissionsService elevatedPermissions) {
        this.authenticationConfigurationManager = authenticationConfigurationManager;
        this.consumerService = consumerService;
        this.consumerTokenStoreService = consumerTokenStoreService;
        this.hostApplication = hostApplication;
        this.oAuthAutoConfigurator = oAuthAutoConfigurator;
        this.requestFactory = requestFactory;
        this.userManager = userManager;
        this.serviceProviderStoreService = serviceProviderStoreService;
        this.elevatedPermissions = elevatedPermissions;
    }

    public AuthenticationProvider getAuthenticationProvider(ApplicationLink link) {
        OAuthAuthenticationProvider provider = null;
        if (this.authenticationConfigurationManager.isConfigured(link.getId(), OAuthAuthenticationProvider.class)) {
            provider = username -> new ThreeLeggedOAuthRequestFactoryImpl(link, this.authenticationConfigurationManager, this.consumerService, this.consumerTokenStoreService, this.requestFactory, this.userManager, (HostApplication)this.hostApplication);
        } else {
            log.debug("OAuthAuthenticationProvider is not configured.");
        }
        return provider;
    }

    public String getConfigUrl(ApplicationLink link, Version applicationLinksVersion, AuthenticationDirection direction, HttpServletRequest request) {
        boolean supportsAppLinks = applicationLinksVersion != null;
        boolean oAuthPluginInstalled = OAuthHelper.isOAuthPluginInstalled(link);
        String configUri = direction == AuthenticationDirection.OUTBOUND ? RequestUtil.getBaseURLFromRequest((HttpServletRequest)request, (URI)this.hostApplication.getBaseUrl()) + OUTBOUND_ATLASSIAN_REDIRECT_LOCATION + link.getId().toString() + "?" + "supportsAppLinks" + "=" + supportsAppLinks : (supportsAppLinks || oAuthPluginInstalled ? RequestUtil.getBaseURLFromRequest((HttpServletRequest)request, (URI)this.hostApplication.getBaseUrl()) + ADD_CONSUMER_BY_URL_SERVLET_LOCATION + link.getId().toString() + "?" + "uiposition" + "=local" : RequestUtil.getBaseURLFromRequest((HttpServletRequest)request, (URI)this.hostApplication.getBaseUrl()) + ADD_CONSUMER_MANUALLY_SERVLET_LOCATION + link.getId().toString());
        return configUri;
    }

    public Class<? extends AuthenticationProvider> getAuthenticationProviderClass() {
        return OAuthAuthenticationProvider.class;
    }

    public boolean isApplicable(AuthenticationScenario authenticationScenario, ApplicationLink applicationLink) {
        return authenticationScenario.isTrusted() && !authenticationScenario.isCommonUserBase();
    }

    public boolean isApplicable(String certificateType) {
        return OrphanedTrustCertificate.Type.OAUTH.name().equals(certificateType);
    }

    public void enable(final RequestFactory authenticatedRequestFactory, final ApplicationLink applicationLink) throws AuthenticationConfigurationException {
        this.executeAsSysAdmin(new Callable<Void>(){

            @Override
            public Void call() throws Exception {
                OAuthAuthenticatorProviderPluginModule.this.oAuthAutoConfigurator.enable(OAuthConfig.createDefaultOAuthConfig(), applicationLink, authenticatedRequestFactory);
                return null;
            }
        });
    }

    public void disable(final RequestFactory authenticatedRequestFactory, final ApplicationLink applicationLink) throws AuthenticationConfigurationException {
        this.executeAsSysAdmin(new Callable<Void>(){

            @Override
            public Void call() throws Exception {
                OAuthAuthenticatorProviderPluginModule.this.oAuthAutoConfigurator.disable(applicationLink, authenticatedRequestFactory);
                return null;
            }
        });
    }

    public boolean incomingEnabled(ApplicationLink applicationLink) {
        return this.serviceProviderStoreService.getConsumer(applicationLink) != null;
    }

    private void executeAsSysAdmin(Callable<Void> callable) throws AuthenticationConfigurationException {
        try {
            this.elevatedPermissions.executeAs(PermissionLevel.SYSADMIN, callable);
        }
        catch (Exception e) {
            Throwables.propagateIfInstanceOf((Throwable)e, AuthenticationConfigurationException.class);
            throw new AuthenticationConfigurationException((Throwable)e);
        }
    }
}

