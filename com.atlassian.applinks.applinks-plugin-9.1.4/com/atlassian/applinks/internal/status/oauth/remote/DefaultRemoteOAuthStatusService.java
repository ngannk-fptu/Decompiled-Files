/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.auth.Anonymous
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.application.ApplicationIdUtil
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.sal.api.net.ResponseException
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.status.oauth.remote;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.auth.Anonymous;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.applink.ApplinkHelper;
import com.atlassian.applinks.internal.common.capabilities.ApplinksCapabilities;
import com.atlassian.applinks.internal.common.capabilities.RemoteApplicationCapabilities;
import com.atlassian.applinks.internal.common.capabilities.RemoteCapabilitiesService;
import com.atlassian.applinks.internal.common.exception.InvalidArgumentException;
import com.atlassian.applinks.internal.common.exception.NoAccessException;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.applinks.internal.permission.PermissionValidationService;
import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import com.atlassian.applinks.internal.status.error.NetworkErrorTranslator;
import com.atlassian.applinks.internal.status.error.SimpleApplinkStatusException;
import com.atlassian.applinks.internal.status.oauth.ApplinkOAuthStatus;
import com.atlassian.applinks.internal.status.oauth.remote.ApplinkAuthenticationOAuthFetchStrategy;
import com.atlassian.applinks.internal.status.oauth.remote.OAuthConnectionVerifier;
import com.atlassian.applinks.internal.status.oauth.remote.OAuthStatusFetchStrategy;
import com.atlassian.applinks.internal.status.oauth.remote.OAuthStatusFetchStrategyChain;
import com.atlassian.applinks.internal.status.oauth.remote.RemoteOAuthStatusService;
import com.atlassian.applinks.internal.status.oauth.remote.StatusApiOAuthFetchStrategy;
import com.atlassian.applinks.internal.status.remote.NoOutgoingAuthenticationException;
import com.atlassian.applinks.internal.status.remote.NoRemoteApplinkException;
import com.atlassian.applinks.spi.application.ApplicationIdUtil;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.sal.api.net.ResponseException;
import java.net.URI;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultRemoteOAuthStatusService
implements RemoteOAuthStatusService {
    private static final EnumSet<ApplinkErrorType> INCOMPATIBLE_LINKS = EnumSet.of(ApplinkErrorType.GENERIC_LINK, ApplinkErrorType.NON_ATLASSIAN, ApplinkErrorType.REMOTE_VERSION_INCOMPATIBLE);
    private static final String FAILED_TO_FETCH_OAUTH_STATUS_MESSAGE = "Failed to fetch OAuth status";
    private final ApplinkHelper applinkHelper;
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private final InternalHostApplication internalHostApplication;
    private final OAuthConnectionVerifier oAuthConnectionVerifier;
    private final PermissionValidationService permissionValidationService;
    private final RemoteCapabilitiesService remoteCapabilitiesService;

    @Autowired
    public DefaultRemoteOAuthStatusService(ApplinkHelper applinkHelper, AuthenticationConfigurationManager authenticationConfigurationManager, InternalHostApplication internalHostApplication, OAuthConnectionVerifier oAuthConnectionVerifier, PermissionValidationService permissionValidationService, RemoteCapabilitiesService remoteCapabilitiesService) {
        this.applinkHelper = applinkHelper;
        this.authenticationConfigurationManager = authenticationConfigurationManager;
        this.internalHostApplication = internalHostApplication;
        this.oAuthConnectionVerifier = oAuthConnectionVerifier;
        this.permissionValidationService = permissionValidationService;
        this.remoteCapabilitiesService = remoteCapabilitiesService;
    }

    @Override
    @Nonnull
    public ApplinkOAuthStatus fetchOAuthStatus(@Nonnull ApplicationId id) throws NoSuchApplinkException, NoAccessException, ApplinkStatusException {
        return this.fetchOAuthStatus(this.applinkHelper.getApplicationLink(id));
    }

    @Override
    @Nonnull
    public ApplinkOAuthStatus fetchOAuthStatus(@Nonnull ApplicationLink link) throws ApplinkStatusException, NoAccessException {
        this.permissionValidationService.validateAdmin();
        try {
            return this.fetchInternal(link);
        }
        catch (ResponseException e) {
            throw NetworkErrorTranslator.toApplinkErrorException(e, FAILED_TO_FETCH_OAUTH_STATUS_MESSAGE);
        }
    }

    private ApplinkOAuthStatus fetchInternal(@Nonnull ApplicationLink applink) throws ResponseException, NoRemoteApplinkException, NoAccessException {
        RemoteApplicationCapabilities capabilities = this.getCapabilities(applink, 1L, TimeUnit.HOURS);
        OAuthStatusFetchStrategy fetchStrategy = this.getFetchStrategy(applink, capabilities);
        ApplinkOAuthStatus status = fetchStrategy.fetch(this.internalHostApplication.getId(), applink);
        if (status != null) {
            return status;
        }
        status = fetchStrategy.fetch(this.generateFallbackId(), applink);
        if (status != null) {
            return status;
        }
        throw new NoRemoteApplinkException(applink.getRpcUrl() + " does not have Application Link to the local application");
    }

    private RemoteApplicationCapabilities getCapabilities(@Nonnull ApplicationLink applink, long maxAge, TimeUnit unit) throws NoAccessException {
        try {
            return this.remoteCapabilitiesService.getCapabilities(applink, maxAge, unit);
        }
        catch (InvalidArgumentException e) {
            throw new AssertionError("Unexpected InvalidArgumentException when getting capabilities", e);
        }
    }

    private OAuthStatusFetchStrategy getFetchStrategy(ApplicationLink applink, RemoteApplicationCapabilities capabilities) {
        if (DefaultRemoteOAuthStatusService.isIncompatible(capabilities)) {
            throw new SimpleApplinkStatusException(capabilities.getError().getType());
        }
        if (capabilities.getCapabilities().contains((Object)ApplinksCapabilities.STATUS_API)) {
            return new StatusApiOAuthFetchStrategy(Anonymous.class);
        }
        Class<? extends AuthenticationProvider> provider = this.getAuthProvider(applink);
        if (capabilities.getApplinksVersion() != null && capabilities.getApplinksVersion().getMajor() >= 5) {
            return new ApplinkAuthenticationOAuthFetchStrategy.For5x(provider, this.oAuthConnectionVerifier);
        }
        if (capabilities.getApplinksVersion() != null && capabilities.getApplinksVersion().getMajor() == 4) {
            return new ApplinkAuthenticationOAuthFetchStrategy.For4x(provider, this.oAuthConnectionVerifier);
        }
        if (capabilities.getApplinksVersion() != null) {
            throw new SimpleApplinkStatusException(ApplinkErrorType.REMOTE_VERSION_INCOMPATIBLE);
        }
        return new OAuthStatusFetchStrategyChain(new ApplinkAuthenticationOAuthFetchStrategy.For5x(provider, this.oAuthConnectionVerifier), new ApplinkAuthenticationOAuthFetchStrategy.For4x(provider, this.oAuthConnectionVerifier));
    }

    private static boolean isIncompatible(RemoteApplicationCapabilities capabilities) {
        return capabilities.hasError() && INCOMPATIBLE_LINKS.contains((Object)capabilities.getError().getType());
    }

    private ApplicationId generateFallbackId() {
        return ApplicationIdUtil.generate((URI)this.internalHostApplication.getBaseUrl());
    }

    private Class<? extends AuthenticationProvider> getAuthProvider(ApplicationLink link) {
        if (this.authenticationConfigurationManager.isConfigured(link.getId(), TwoLeggedOAuthWithImpersonationAuthenticationProvider.class)) {
            return TwoLeggedOAuthWithImpersonationAuthenticationProvider.class;
        }
        if (this.authenticationConfigurationManager.isConfigured(link.getId(), OAuthAuthenticationProvider.class)) {
            return OAuthAuthenticationProvider.class;
        }
        throw new NoOutgoingAuthenticationException("Neither 3LO nor 2LOi auth configured");
    }
}

