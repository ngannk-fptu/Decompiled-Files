/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.auth.types.BasicAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TrustedAppsAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationException
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.migration.remote;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.auth.types.BasicAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TrustedAppsAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.auth.oauth.OAuthAutoConfigurator;
import com.atlassian.applinks.internal.common.exception.RemoteMigrationInvalidResponseException;
import com.atlassian.applinks.internal.common.exception.ServiceExceptionFactory;
import com.atlassian.applinks.internal.common.status.oauth.OAuthConfig;
import com.atlassian.applinks.internal.migration.AuthenticationConfig;
import com.atlassian.applinks.internal.migration.AuthenticationStatus;
import com.atlassian.applinks.internal.migration.remote.DisableTrustedApp;
import com.atlassian.applinks.internal.migration.remote.OAuthConfigMigrator;
import com.atlassian.applinks.internal.migration.remote.QueryLegacyAuthentication;
import com.atlassian.applinks.internal.migration.remote.QuerySysAdminAccess;
import com.atlassian.applinks.internal.migration.remote.TryWithAuthentication;
import com.atlassian.applinks.internal.migration.remote.TryWithCredentials;
import com.atlassian.applinks.internal.status.DefaultLegacyConfig;
import com.atlassian.applinks.internal.status.LegacyConfig;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationException;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.io.Serializable;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class RemoteMigrationHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteMigrationHelper.class);
    private static final TryWithAuthentication DISABLE_TRUSTED = DisableTrustedApp.INSTANCE;
    private static final TryWithAuthentication QUERY_SYS_ADMIN_ACCESS = QuerySysAdminAccess.INSTANCE;
    private static final QueryLegacyAuthentication.Factory QUERY_LEGACY_AUTHENTICATION_FACTORY = new QueryLegacyAuthentication.Factory();
    private static final OAuthConfigMigrator.Factory OAUTH_CONFIGURATOR_FACTORY = new OAuthConfigMigrator.Factory();
    private final OAuthAutoConfigurator oauthAutoConfigurator;
    private final ServiceExceptionFactory serviceExceptionFactory;
    private final InternalHostApplication internalHostApplication;

    @Autowired
    public RemoteMigrationHelper(OAuthAutoConfigurator oauthAutoConfigurator, ServiceExceptionFactory serviceExceptionFactory, InternalHostApplication internalHostApplication) {
        this.oauthAutoConfigurator = oauthAutoConfigurator;
        this.serviceExceptionFactory = serviceExceptionFactory;
        this.internalHostApplication = internalHostApplication;
    }

    public boolean disableRemoteTrustedApp(@Nonnull ApplicationLink applicationLink) throws RemoteMigrationInvalidResponseException {
        return this.tryWithAuthentications(applicationLink, DISABLE_TRUSTED);
    }

    public boolean hasSysAdminAccess(@Nonnull ApplicationLink applicationLink) throws RemoteMigrationInvalidResponseException {
        return this.tryWithAuthentications(applicationLink, QUERY_SYS_ADMIN_ACCESS);
    }

    @Nonnull
    public LegacyConfig getLegacyConfig(@Nonnull ApplicationLink applicationLink) throws RemoteMigrationInvalidResponseException {
        QueryLegacyAuthentication queryLegacyAuthentication = QUERY_LEGACY_AUTHENTICATION_FACTORY.getInstance();
        if (this.tryWithAuthentications(applicationLink, queryLegacyAuthentication)) {
            return queryLegacyAuthentication.getLegacyConfig();
        }
        return new DefaultLegacyConfig();
    }

    @Nonnull
    public AuthenticationStatus migrate(@Nonnull ApplicationLink applicationLink, @Nonnull AuthenticationStatus status) throws RemoteMigrationInvalidResponseException {
        OAuthConfig outgoingOAuth;
        OAuthConfig incomingOAuth = this.createMigrationOAuthConfig(status.incoming());
        OAuthConfigMigrator oauthConfigMigrator = OAUTH_CONFIGURATOR_FACTORY.getInstance(this.oauthAutoConfigurator, incomingOAuth, outgoingOAuth = this.createMigrationOAuthConfig(status.outgoing()));
        if (this.tryWithAuthentications(applicationLink, oauthConfigMigrator)) {
            return status.outgoing(status.outgoing().oauth(outgoingOAuth)).incoming(status.incoming().oauth(incomingOAuth));
        }
        return status;
    }

    private OAuthConfig createMigrationOAuthConfig(AuthenticationConfig config) {
        if (config.isOAuthConfigured()) {
            return config.getOAuthConfig();
        }
        if (config.isTrustedConfigured()) {
            return OAuthConfig.createOAuthWithImpersonationConfig();
        }
        if (config.isBasicConfigured()) {
            return OAuthConfig.createDefaultOAuthConfig();
        }
        return config.getOAuthConfig();
    }

    @VisibleForTesting
    protected boolean tryWithAuthentications(ApplicationLink applicationLink, TryWithAuthentication tryWithAuthentication) throws RemoteMigrationInvalidResponseException {
        ApplicationId localApplicationId = this.internalHostApplication.getId();
        try {
            TryWithCredentials tryWithCredentials = new TryWithCredentials(tryWithAuthentication);
            boolean addedRemotely = tryWithCredentials.execute(applicationLink, localApplicationId, BasicAuthenticationProvider.class);
            addedRemotely = addedRemotely || tryWithCredentials.execute(applicationLink, localApplicationId, TrustedAppsAuthenticationProvider.class);
            addedRemotely = addedRemotely || tryWithCredentials.execute(applicationLink, localApplicationId, TwoLeggedOAuthWithImpersonationAuthenticationProvider.class);
            addedRemotely = addedRemotely || tryWithCredentials.execute(applicationLink, localApplicationId, TwoLeggedOAuthAuthenticationProvider.class);
            addedRemotely = addedRemotely || tryWithCredentials.execute(applicationLink, localApplicationId, OAuthAuthenticationProvider.class);
            return addedRemotely;
        }
        catch (AuthenticationConfigurationException | ResponseException | IOException ex) {
            throw this.serviceExceptionFactory.create(RemoteMigrationInvalidResponseException.class, new Serializable[]{ex});
        }
    }
}

