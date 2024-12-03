/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.internal.common.auth.oauth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider;
import com.atlassian.applinks.internal.common.auth.oauth.ConsumerInformationHelper;
import com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService;
import com.atlassian.applinks.internal.common.auth.oauth.ServiceProviderStoreService;
import com.atlassian.applinks.internal.common.auth.oauth.util.Consumers;
import com.atlassian.applinks.internal.common.exception.ConsumerInformationUnavailableException;
import com.atlassian.applinks.internal.common.exception.ServiceExceptionFactory;
import com.atlassian.applinks.internal.common.permission.Unrestricted;
import com.atlassian.applinks.internal.common.status.oauth.OAuthConfig;
import com.atlassian.applinks.internal.status.oauth.OAuthConfigs;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.oauth.Consumer;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Unrestricted(value="Consumers of this component need to enforce appropriate permission level")
public class OAuthConfigurator {
    private static final Logger log = LoggerFactory.getLogger(OAuthConfigurator.class);
    private final ConsumerTokenStoreService consumerTokenStoreService;
    private final ServiceProviderStoreService serviceProviderStoreService;
    private final ServiceExceptionFactory serviceExceptionFactory;
    private final AuthenticationConfigurationManager authenticationConfigurationManager;

    public OAuthConfigurator(AuthenticationConfigurationManager authenticationConfigurationManager, ConsumerTokenStoreService consumerTokenStoreService, ServiceProviderStoreService serviceProviderStoreService, ServiceExceptionFactory serviceExceptionFactory) {
        this.consumerTokenStoreService = consumerTokenStoreService;
        this.serviceProviderStoreService = serviceProviderStoreService;
        this.authenticationConfigurationManager = authenticationConfigurationManager;
        this.serviceExceptionFactory = serviceExceptionFactory;
    }

    @Nonnull
    public OAuthConfig getIncomingConfig(@Nonnull ApplicationLink applink) {
        Objects.requireNonNull(applink, "applink");
        return OAuthConfigs.fromConsumer(this.serviceProviderStoreService.getConsumer(applink));
    }

    @Nonnull
    public OAuthConfig getOutgoingConfig(@Nonnull ApplicationLink link) {
        Objects.requireNonNull(link, "link");
        boolean is3LoConfigured = this.authenticationConfigurationManager.isConfigured(link.getId(), OAuthAuthenticationProvider.class);
        boolean is2LoConfigured = this.authenticationConfigurationManager.isConfigured(link.getId(), TwoLeggedOAuthAuthenticationProvider.class);
        boolean is2LoIConfigured = this.authenticationConfigurationManager.isConfigured(link.getId(), TwoLeggedOAuthWithImpersonationAuthenticationProvider.class);
        return OAuthConfig.fromConfig(is3LoConfigured, is2LoConfigured, is2LoIConfigured);
    }

    public void updateOutgoingConfig(@Nonnull ApplicationLink applink, @Nonnull OAuthConfig outgoing) {
        Objects.requireNonNull(applink, "link");
        Objects.requireNonNull(outgoing, "outgoing");
        if (!outgoing.isEnabled()) {
            this.tryRemoveConsumerTokens(applink);
        }
        this.toggleProvider(applink, OAuthAuthenticationProvider.class, outgoing.isEnabled());
        this.toggleProvider(applink, TwoLeggedOAuthAuthenticationProvider.class, outgoing.isTwoLoEnabled());
        this.toggleProvider(applink, TwoLeggedOAuthWithImpersonationAuthenticationProvider.class, outgoing.isTwoLoImpersonationEnabled());
    }

    public void updateIncomingConfig(@Nonnull ApplicationLink applink, @Nonnull OAuthConfig incoming) throws ConsumerInformationUnavailableException {
        Objects.requireNonNull(applink, "applink");
        Objects.requireNonNull(incoming, "incoming");
        if (!incoming.isEnabled()) {
            try {
                this.serviceProviderStoreService.removeConsumer(applink);
            }
            catch (IllegalStateException e) {
                log.debug("Attempting to remove non-existing consumer for Application Link '{}'", (Object)applink);
                log.trace("Stack trace for link '{}'", (Object)applink, (Object)e);
            }
        } else {
            Consumer updatedConsumer = Consumers.consumerBuilder(this.getOrFetchConsumer(applink)).twoLOAllowed(incoming.isTwoLoEnabled()).twoLOImpersonationAllowed(incoming.isTwoLoImpersonationEnabled()).build();
            this.serviceProviderStoreService.addConsumer(updatedConsumer, applink);
        }
    }

    private void tryRemoveConsumerTokens(@Nonnull ApplicationLink applink) {
        try {
            this.consumerTokenStoreService.removeAllConsumerTokens(applink);
        }
        catch (IllegalStateException illegalStateException) {
            // empty catch block
        }
    }

    private void toggleProvider(ApplicationLink link, Class<? extends AuthenticationProvider> providerClass, boolean enabled) {
        if (enabled) {
            this.authenticationConfigurationManager.registerProvider(link.getId(), providerClass, Collections.emptyMap());
        } else {
            this.authenticationConfigurationManager.unregisterProvider(link.getId(), providerClass);
        }
    }

    private Consumer getOrFetchConsumer(ApplicationLink link) throws ConsumerInformationUnavailableException {
        Consumer consumer = this.serviceProviderStoreService.getConsumer(link);
        if (consumer != null) {
            return consumer;
        }
        return this.fetchConsumerInformation(link);
    }

    @VisibleForTesting
    protected Consumer fetchConsumerInformation(ApplicationLink link) throws ConsumerInformationUnavailableException {
        try {
            return ConsumerInformationHelper.fetchConsumerInformation(link);
        }
        catch (ResponseException e) {
            throw this.serviceExceptionFactory.create(ConsumerInformationUnavailableException.class, new Serializable[]{link.getName()});
        }
    }
}

