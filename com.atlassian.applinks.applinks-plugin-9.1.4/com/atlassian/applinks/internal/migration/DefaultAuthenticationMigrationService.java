/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.BasicAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TrustedAppsAuthenticationProvider
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.migration;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.types.BasicAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TrustedAppsAuthenticationProvider;
import com.atlassian.applinks.internal.applink.ApplinkHelper;
import com.atlassian.applinks.internal.common.auth.trusted.ApplinksTrustedApps;
import com.atlassian.applinks.internal.common.exception.InvalidEntityStateException;
import com.atlassian.applinks.internal.common.exception.NoAccessException;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.applinks.internal.common.exception.RemoteMigrationInvalidResponseException;
import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.atlassian.applinks.internal.common.exception.ServiceExceptionFactory;
import com.atlassian.applinks.internal.common.i18n.I18nKey;
import com.atlassian.applinks.internal.migration.AuthenticationConfig;
import com.atlassian.applinks.internal.migration.AuthenticationMigrationService;
import com.atlassian.applinks.internal.migration.AuthenticationStatus;
import com.atlassian.applinks.internal.migration.OAuthMigrationUtil;
import com.atlassian.applinks.internal.migration.remote.RemoteMigrationHelper;
import com.atlassian.applinks.internal.permission.PermissionValidationService;
import com.atlassian.applinks.internal.status.DefaultLegacyConfig;
import com.atlassian.applinks.internal.status.LegacyConfig;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import com.atlassian.applinks.internal.status.oauth.ApplinkOAuthStatus;
import com.atlassian.applinks.internal.status.oauth.OAuthStatusService;
import com.atlassian.applinks.internal.status.oauth.remote.RemoteOAuthStatusService;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultAuthenticationMigrationService
implements AuthenticationMigrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAuthenticationMigrationService.class);
    private final ApplinkHelper applinkHelper;
    private final OAuthStatusService oAuthStatusService;
    private final ServiceExceptionFactory serviceExceptionFactory;
    private final PermissionValidationService permissionValidationService;
    private final AuthenticationConfigurationManager authConfigManager;
    private final RemoteOAuthStatusService remoteOAuthStatusService;
    private final RemoteMigrationHelper remoteMigrationHelper;

    @Autowired
    public DefaultAuthenticationMigrationService(ApplinkHelper applinkHelper, OAuthStatusService oAuthStatusService, RemoteOAuthStatusService remoteOAuthStatusService, RemoteMigrationHelper remoteMigrationHelper, ServiceExceptionFactory serviceExceptionFactory, PermissionValidationService permissionValidationService, AuthenticationConfigurationManager authenticationConfigurationManager) {
        this.applinkHelper = applinkHelper;
        this.oAuthStatusService = oAuthStatusService;
        this.remoteOAuthStatusService = remoteOAuthStatusService;
        this.remoteMigrationHelper = remoteMigrationHelper;
        this.serviceExceptionFactory = serviceExceptionFactory;
        this.permissionValidationService = permissionValidationService;
        this.authConfigManager = authenticationConfigurationManager;
    }

    @Override
    @Nonnull
    public AuthenticationStatus migrateToOAuth(@Nonnull ApplicationId applicationId) throws ServiceException {
        Objects.requireNonNull(applicationId, "applicationId");
        this.permissionValidationService.validateAdmin();
        ApplicationLink link = this.applinkHelper.getApplicationLink(applicationId);
        AuthenticationStatus configs = this.getAuthenticationMigrationStatus(link);
        ApplinkOAuthStatus remoteOAuthStatus = this.fetchRemoteOAuthStatus(link);
        MigrationState migrationState = new MigrationState(link, configs, remoteOAuthStatus, this.hasRemoteSysAdminAccess(link));
        migrationState = this.migrate(migrationState);
        migrationState = migrationState.remoteOAuthStatus(this.fetchRemoteOAuthStatus(link));
        Boolean disableRemoteTrustedAttempted = this.removeInbound(migrationState);
        migrationState = this.removeOutbound(migrationState, disableRemoteTrustedAttempted);
        return migrationState.authenticationStatus;
    }

    @Override
    public boolean hasRemoteSysAdminAccess(@Nonnull ApplicationLink link) throws NoSuchApplinkException, NoAccessException {
        try {
            Objects.requireNonNull(link, "link");
            this.permissionValidationService.validateAdmin();
            return this.remoteMigrationHelper.hasSysAdminAccess(link);
        }
        catch (RemoteMigrationInvalidResponseException e) {
            LOGGER.debug("Failed to check for remote sys admin access ", (Throwable)e);
            return false;
        }
    }

    private MigrationState migrate(MigrationState migrationState) throws ServiceException {
        if (migrationState.authenticationStatus.outgoing().isTrustedConfigured() || migrationState.authenticationStatus.incoming().isTrustedConfigured()) {
            this.permissionValidationService.validateSysadmin();
        }
        if (this.oAuthMismatch(migrationState)) {
            throw this.serviceExceptionFactory.create(InvalidEntityStateException.class, I18nKey.newI18nKey("applinks.service.error.oauth.mismatch.during.migration", new Serializable[0]));
        }
        if (migrationState.remoteSysAdmin && !OAuthMigrationUtil.isOAuthConfigured(migrationState.authenticationStatus)) {
            return migrationState.authenticationStatus(this.remoteMigrationHelper.migrate(migrationState.link, migrationState.authenticationStatus));
        }
        return migrationState;
    }

    private boolean oAuthMismatch(MigrationState migrationState) {
        ApplinkOAuthStatus localOAuthStatus = new ApplinkOAuthStatus(migrationState.authenticationStatus.incoming().getOAuthConfig(), migrationState.authenticationStatus.outgoing().getOAuthConfig());
        return !localOAuthStatus.matches(migrationState.remoteOAuthStatus);
    }

    private LegacyConfig getLocalLegacyConfig(@Nonnull ApplicationLink link) {
        return new DefaultLegacyConfig().basic(this.authConfigManager.isConfigured(link.getId(), BasicAuthenticationProvider.class)).trusted(this.authConfigManager.isConfigured(link.getId(), TrustedAppsAuthenticationProvider.class));
    }

    private MigrationState removeOutbound(MigrationState migrationState, Boolean disableRemoteTrustedAttempted) throws ServiceException {
        AuthenticationConfig outgoing = migrationState.authenticationStatus.outgoing();
        if (outgoing.isOAuthConfigured() && migrationState.remoteOAuthStatus.getIncoming().isEnabled()) {
            if (outgoing.isTrustedConfigured()) {
                boolean remoteTrustedRemoved = disableRemoteTrustedAttempted == null ? this.remoteMigrationHelper.disableRemoteTrustedApp(migrationState.link) : disableRemoteTrustedAttempted.booleanValue();
                this.checkTrustedRemoved(migrationState, remoteTrustedRemoved);
                if (remoteTrustedRemoved) {
                    this.removeProvider(migrationState.link, TrustedAppsAuthenticationProvider.class);
                }
                outgoing = outgoing.trustedConfigured(!remoteTrustedRemoved);
            }
            this.removeProvider(migrationState.link, BasicAuthenticationProvider.class);
            outgoing = outgoing.basicConfigured(false);
        }
        return migrationState.authenticationStatus(migrationState.authenticationStatus.outgoing(outgoing));
    }

    private void checkTrustedRemoved(MigrationState migrationState, boolean remoteTrustedRemoved) throws ServiceException {
        if (migrationState.remoteSysAdmin && !remoteTrustedRemoved) {
            throw this.serviceExceptionFactory.create(InvalidEntityStateException.class, I18nKey.newI18nKey("applinks.service.error.remote.disable.trusted.invalid", new Serializable[0]));
        }
    }

    private Boolean removeInbound(MigrationState migrationState) throws ServiceException {
        AuthenticationConfig incoming = migrationState.authenticationStatus.incoming();
        if (incoming.isOAuthConfigured() && migrationState.remoteOAuthStatus.getOutgoing().isEnabled() && incoming.isTrustedConfigured()) {
            boolean remoteTrustedDisabled = this.remoteMigrationHelper.disableRemoteTrustedApp(migrationState.link);
            this.checkTrustedRemoved(migrationState, remoteTrustedDisabled);
            return remoteTrustedDisabled;
        }
        return null;
    }

    private AuthenticationStatus getAuthenticationMigrationStatus(@Nonnull ApplicationLink link, @Nonnull ApplinkOAuthStatus localOAuthStatus, @Nonnull LegacyConfig remoteLegacyConfig) {
        Objects.requireNonNull(localOAuthStatus, "localOAuthStatus");
        Objects.requireNonNull(remoteLegacyConfig, "remoteLegacyConfig");
        LegacyConfig localLegacyConfig = this.getLocalLegacyConfig(link);
        AuthenticationConfig outgoing = new AuthenticationConfig(localOAuthStatus.getOutgoing(), localLegacyConfig.isBasicConfigured(), localLegacyConfig.isTrustedConfigured());
        AuthenticationConfig incoming = new AuthenticationConfig(localOAuthStatus.getIncoming(), remoteLegacyConfig.isBasicConfigured(), link.getProperty(ApplinksTrustedApps.PROPERTY_TRUSTED_APPS_INCOMING_ID) != null);
        return new AuthenticationStatus(incoming, outgoing);
    }

    @Override
    @Nonnull
    public AuthenticationStatus getAuthenticationMigrationStatus(@Nonnull ApplicationLink link, @Nonnull ApplinkOAuthStatus localOAuthStatus) throws NoSuchApplinkException, NoAccessException {
        LegacyConfig remoteLegacyConfig;
        Objects.requireNonNull(localOAuthStatus, "localOAuthStatus");
        this.permissionValidationService.validateAdmin();
        try {
            remoteLegacyConfig = this.remoteMigrationHelper.getLegacyConfig(link);
        }
        catch (RemoteMigrationInvalidResponseException e) {
            remoteLegacyConfig = new DefaultLegacyConfig();
        }
        return this.getAuthenticationMigrationStatus(link, localOAuthStatus, remoteLegacyConfig);
    }

    private AuthenticationStatus getAuthenticationMigrationStatus(ApplicationLink link) throws RemoteMigrationInvalidResponseException {
        ApplinkOAuthStatus oauthStatus = this.oAuthStatusService.getOAuthStatus(link);
        return this.getAuthenticationMigrationStatus(link, oauthStatus, this.remoteMigrationHelper.getLegacyConfig(link));
    }

    private ApplinkOAuthStatus fetchRemoteOAuthStatus(ApplicationLink link) {
        try {
            return this.remoteOAuthStatusService.fetchOAuthStatus(link);
        }
        catch (NoAccessException | ApplinkStatusException ex) {
            LOGGER.debug("Failed to fetch remote oauth status.", (Throwable)ex);
            return ApplinkOAuthStatus.OFF;
        }
    }

    private void removeProvider(ApplicationLink applicationLink, Class<? extends AuthenticationProvider> providerClass) {
        this.authConfigManager.unregisterProvider(applicationLink.getId(), providerClass);
    }

    private class MigrationState {
        final ApplicationLink link;
        final AuthenticationStatus authenticationStatus;
        final boolean remoteSysAdmin;
        final ApplinkOAuthStatus remoteOAuthStatus;

        public MigrationState(@Nonnull ApplicationLink link, @Nonnull AuthenticationStatus authenticationStatus, ApplinkOAuthStatus remoteOAuthStatus, boolean remoteSysAdmin) {
            this.link = Objects.requireNonNull(link, "link");
            this.authenticationStatus = Objects.requireNonNull(authenticationStatus, "authenticationStatus");
            this.remoteOAuthStatus = Objects.requireNonNull(remoteOAuthStatus, "remoteOAuthStatus");
            this.remoteSysAdmin = remoteSysAdmin;
        }

        @Nonnull
        public MigrationState authenticationStatus(@Nonnull AuthenticationStatus authenticationStatus) {
            Objects.requireNonNull(authenticationStatus, "authenticationStatus");
            return new MigrationState(this.link, authenticationStatus, this.remoteOAuthStatus, this.remoteSysAdmin);
        }

        @Nonnull
        public MigrationState remoteOAuthStatus(@Nonnull ApplinkOAuthStatus remoteOAuthStatus) {
            Objects.requireNonNull(remoteOAuthStatus, "remoteOAuthStatus");
            return new MigrationState(this.link, this.authenticationStatus, remoteOAuthStatus, this.remoteSysAdmin);
        }
    }
}

