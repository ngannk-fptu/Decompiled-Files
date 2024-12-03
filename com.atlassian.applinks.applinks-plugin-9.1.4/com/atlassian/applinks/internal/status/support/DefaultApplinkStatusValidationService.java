/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.status.support;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.common.capabilities.ApplicationVersion;
import com.atlassian.applinks.internal.common.capabilities.ApplinksCapabilities;
import com.atlassian.applinks.internal.common.capabilities.RemoteApplicationCapabilities;
import com.atlassian.applinks.internal.common.capabilities.RemoteCapabilitiesService;
import com.atlassian.applinks.internal.common.exception.InvalidArgumentException;
import com.atlassian.applinks.internal.common.exception.NoAccessException;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.applinks.internal.common.status.oauth.OAuthConfig;
import com.atlassian.applinks.internal.migration.AuthenticationMigrationService;
import com.atlassian.applinks.internal.migration.AuthenticationStatus;
import com.atlassian.applinks.internal.migration.OAuthMigrationUtil;
import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import com.atlassian.applinks.internal.status.error.SimpleApplinkStatusException;
import com.atlassian.applinks.internal.status.oauth.ApplinkOAuthStatus;
import com.atlassian.applinks.internal.status.support.ApplinkCompatibilityVerifier;
import com.atlassian.applinks.internal.status.support.ApplinkStatusValidationService;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultApplinkStatusValidationService
implements ApplinkStatusValidationService {
    private static final ApplicationVersion MIN_VERSION = ApplicationVersion.parse("4.0.13");
    private final ApplinkCompatibilityVerifier compatibilityVerifier;
    private final RemoteCapabilitiesService remoteCapabilitiesService;
    private final AuthenticationMigrationService authenticationMigrationService;

    @Autowired
    public DefaultApplinkStatusValidationService(ApplinkCompatibilityVerifier compatibilityVerifier, AuthenticationMigrationService authenticationMigrationService, RemoteCapabilitiesService remoteCapabilitiesService) {
        this.compatibilityVerifier = compatibilityVerifier;
        this.authenticationMigrationService = authenticationMigrationService;
        this.remoteCapabilitiesService = remoteCapabilitiesService;
    }

    @Override
    public void checkLocalCompatibility(@Nonnull ApplicationLink link) {
        ApplinkErrorType compatibilityError = this.compatibilityVerifier.verifyLocalCompatibility(link);
        if (compatibilityError != null) {
            throw new StatusError(compatibilityError);
        }
    }

    @Override
    public void checkVersionCompatibility(@Nonnull ApplicationLink link) throws NoAccessException {
        RemoteApplicationCapabilities capabilities = this.getCapabilities(link);
        if (capabilities.getApplinksVersion() != null && capabilities.getApplinksVersion().compareTo(MIN_VERSION) < 0) {
            throw new StatusError(ApplinkErrorType.REMOTE_VERSION_INCOMPATIBLE);
        }
    }

    @Override
    public void checkOAuthSupportedCompatibility(@Nonnull ApplinkOAuthStatus status) {
        this.checkOAuthSupported(status.getIncoming());
        this.checkOAuthSupported(status.getOutgoing());
    }

    @Override
    public void checkOAuthMismatch(@Nonnull ApplinkOAuthStatus localStatus, @Nonnull ApplinkOAuthStatus remoteStatus) {
        Objects.requireNonNull(localStatus, "localAuthentication");
        Objects.requireNonNull(localStatus, "remoteAuthentication");
        if (!localStatus.matches(remoteStatus)) {
            throw new StatusError(ApplinkErrorType.AUTH_LEVEL_MISMATCH);
        }
    }

    @Override
    public void checkLegacyAuthentication(@Nonnull ApplicationLink link, @Nonnull ApplinkOAuthStatus localStatus, @Nonnull ApplinkOAuthStatus remoteStatus) throws NoSuchApplinkException, NoAccessException {
        Objects.requireNonNull(localStatus, "localStatus");
        Objects.requireNonNull(remoteStatus, "remoteStatus");
        AuthenticationStatus authenticationStatus = this.authenticationMigrationService.getAuthenticationMigrationStatus(link, localStatus);
        if (authenticationStatus.incoming().hasLegacy() || authenticationStatus.outgoing().hasLegacy()) {
            boolean hasRemoteSysAdminAccess = this.authenticationMigrationService.hasRemoteSysAdminAccess(link);
            boolean oAuthConfigured = OAuthMigrationUtil.isOAuthConfigured(authenticationStatus);
            if (hasRemoteSysAdminAccess) {
                if (oAuthConfigured) {
                    throw new StatusError(ApplinkErrorType.LEGACY_REMOVAL);
                }
                throw new StatusError(ApplinkErrorType.LEGACY_UPDATE);
            }
            if (oAuthConfigured) {
                if (authenticationStatus.outgoing().isBasicConfigured() && this.noTrustedConfigured(authenticationStatus)) {
                    throw new StatusError(ApplinkErrorType.LEGACY_REMOVAL);
                }
                if (authenticationStatus.incoming().isTrustedConfigured() && this.noBasicConfigured(authenticationStatus) && this.noMigrationApi(link)) {
                    throw new StatusError(ApplinkErrorType.MANUAL_LEGACY_REMOVAL_WITH_OLD_EDIT);
                }
                throw new StatusError(ApplinkErrorType.MANUAL_LEGACY_REMOVAL);
            }
            throw new StatusError(ApplinkErrorType.MANUAL_LEGACY_UPDATE);
        }
    }

    private boolean noMigrationApi(ApplicationLink link) throws NoAccessException {
        return !this.getCapabilities(link).getCapabilities().contains((Object)ApplinksCapabilities.MIGRATION_API);
    }

    private boolean noBasicConfigured(AuthenticationStatus authenticationStatus) {
        return !authenticationStatus.outgoing().isBasicConfigured() && !authenticationStatus.incoming().isBasicConfigured();
    }

    private boolean noTrustedConfigured(AuthenticationStatus authenticationStatus) {
        return !authenticationStatus.outgoing().isTrustedConfigured() && !authenticationStatus.incoming().isTrustedConfigured();
    }

    @Override
    public void checkDisabled(@Nullable ApplinkOAuthStatus localStatus, @Nullable ApplinkOAuthStatus remoteStatus) {
        ApplinkOAuthStatus local;
        ApplinkOAuthStatus applinkOAuthStatus = local = localStatus == null ? ApplinkOAuthStatus.OFF : localStatus;
        if (local.equals(remoteStatus) && local.equals(ApplinkOAuthStatus.OFF)) {
            throw new StatusError(ApplinkErrorType.DISABLED);
        }
    }

    @Override
    public void checkEditable(@Nonnull ApplicationLink applink) throws NoAccessException, ApplinkStatusException {
        this.checkLocalCompatibility(applink);
        this.checkVersionCompatibility(applink);
    }

    private void checkOAuthSupported(@Nonnull OAuthConfig config) {
        if (config.isEnabled() && !config.isTwoLoEnabled()) {
            throw new StatusError(ApplinkErrorType.AUTH_LEVEL_UNSUPPORTED);
        }
    }

    private RemoteApplicationCapabilities getCapabilities(@Nonnull ApplicationLink link) throws NoAccessException {
        try {
            return this.remoteCapabilitiesService.getCapabilities(link, 1L, TimeUnit.HOURS);
        }
        catch (InvalidArgumentException e) {
            throw new AssertionError("Unexpected InvalidArgumentException", e);
        }
    }

    static final class StatusError
    extends SimpleApplinkStatusException {
        public StatusError(ApplinkErrorType errorType) {
            super(errorType);
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }
}

