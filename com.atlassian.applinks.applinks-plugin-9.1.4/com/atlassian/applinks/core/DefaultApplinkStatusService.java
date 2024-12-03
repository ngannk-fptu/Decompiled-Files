/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Throwables
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.core;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.core.ApplinkStatus;
import com.atlassian.applinks.core.ApplinkStatusService;
import com.atlassian.applinks.core.DefaultApplinkStatus;
import com.atlassian.applinks.internal.applink.ApplinkHelper;
import com.atlassian.applinks.internal.common.capabilities.ApplinksCapabilities;
import com.atlassian.applinks.internal.common.capabilities.RemoteCapabilitiesService;
import com.atlassian.applinks.internal.common.exception.InvalidArgumentException;
import com.atlassian.applinks.internal.common.exception.NoAccessException;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.applinks.internal.common.net.HttpUtils;
import com.atlassian.applinks.internal.permission.PermissionValidationService;
import com.atlassian.applinks.internal.status.error.ApplinkError;
import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.applinks.internal.status.error.ApplinkErrorVisitor;
import com.atlassian.applinks.internal.status.error.AuthorisationUriAwareApplinkError;
import com.atlassian.applinks.internal.status.error.ResponseApplinkError;
import com.atlassian.applinks.internal.status.oauth.ApplinkOAuthStatus;
import com.atlassian.applinks.internal.status.oauth.OAuthStatusService;
import com.atlassian.applinks.internal.status.oauth.remote.OAuthConnectionVerifier;
import com.atlassian.applinks.internal.status.oauth.remote.RemoteOAuthStatusService;
import com.atlassian.applinks.internal.status.support.ApplinkStatusValidationService;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Throwables;
import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultApplinkStatusService
implements ApplinkStatusService {
    private static final Logger log = LoggerFactory.getLogger(DefaultApplinkStatusService.class);
    private static final EnumSet<ApplinkErrorType> TOKEN_PROBLEMS = EnumSet.of(ApplinkErrorType.LOCAL_AUTH_TOKEN_REQUIRED, ApplinkErrorType.REMOTE_AUTH_TOKEN_REQUIRED);
    private final ApplinkHelper applinkHelper;
    private final ApplinkStatusValidationService statusValidationService;
    private final OAuthConnectionVerifier oAuthConnectionVerifier;
    private final OAuthStatusService oAuthStatusService;
    private final PermissionValidationService permissionValidationService;
    private final RemoteCapabilitiesService remoteCapabilitiesService;
    private final RemoteOAuthStatusService remoteOAuthStatusService;
    private final EventPublisher eventPublisher;

    @Autowired
    public DefaultApplinkStatusService(ApplinkHelper applinkHelper, ApplinkStatusValidationService statusValidationService, OAuthConnectionVerifier oAuthConnectionVerifier, OAuthStatusService oAuthStatusService, PermissionValidationService permissionValidationService, RemoteCapabilitiesService remoteCapabilitiesService, RemoteOAuthStatusService remoteOAuthStatusService, EventPublisher eventPublisher) {
        this.applinkHelper = applinkHelper;
        this.statusValidationService = statusValidationService;
        this.oAuthConnectionVerifier = oAuthConnectionVerifier;
        this.oAuthStatusService = oAuthStatusService;
        this.permissionValidationService = permissionValidationService;
        this.remoteCapabilitiesService = remoteCapabilitiesService;
        this.remoteOAuthStatusService = remoteOAuthStatusService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Nonnull
    public ApplinkStatus getApplinkStatus(@Nonnull ApplicationId id) throws NoSuchApplinkException, NoAccessException {
        DefaultApplinkStatus applinkStatus;
        Objects.requireNonNull(id, "id");
        this.permissionValidationService.validateAdmin();
        ApplicationLink link = this.applinkHelper.getApplicationLink(id);
        ApplinkOAuthStatus localStatus = null;
        ApplinkOAuthStatus remoteStatus = null;
        try {
            localStatus = this.oAuthStatusService.getOAuthStatus(link);
            this.statusValidationService.checkLocalCompatibility(link);
            this.statusValidationService.checkVersionCompatibility(link);
            remoteStatus = this.remoteOAuthStatusService.fetchOAuthStatus(link);
            this.statusValidationService.checkOAuthSupportedCompatibility(localStatus);
            this.statusValidationService.checkOAuthSupportedCompatibility(remoteStatus);
            this.statusValidationService.checkOAuthMismatch(localStatus, remoteStatus);
            this.statusValidationService.checkLegacyAuthentication(link, localStatus, remoteStatus);
            this.statusValidationService.checkDisabled(localStatus, remoteStatus);
            this.verifyOAuthOnSuccessfulStatus(link);
            applinkStatus = DefaultApplinkStatus.working(link, localStatus, remoteStatus);
        }
        catch (Exception error) {
            Throwables.propagateIfInstanceOf((Throwable)error, NoSuchApplinkException.class);
            Throwables.propagateIfInstanceOf((Throwable)error, NoAccessException.class);
            applinkStatus = this.createStatus(id, link, localStatus, remoteStatus, this.checkForNetworkError(error, link));
        }
        return applinkStatus;
    }

    private static boolean isTokenProblem(Exception error) {
        return error instanceof ApplinkError && TOKEN_PROBLEMS.contains((Object)((ApplinkError)ApplinkError.class.cast(error)).getType());
    }

    private void verifyOAuthOnSuccessfulStatus(ApplicationLink link) throws NoAccessException {
        if (this.hasStatusApi(link)) {
            this.oAuthConnectionVerifier.verifyOAuthConnection(link);
        }
    }

    private boolean hasStatusApi(ApplicationLink link) throws NoAccessException {
        try {
            return this.remoteCapabilitiesService.getCapabilities(link, 1L, TimeUnit.HOURS).getCapabilities().contains((Object)ApplinksCapabilities.STATUS_API);
        }
        catch (InvalidArgumentException e) {
            throw new AssertionError("Unexpected InvalidArgumentException when getting capabilities", e);
        }
    }

    private Exception checkForNetworkError(Exception originalError, ApplicationLink link) {
        if (DefaultApplinkStatusService.isTokenProblem(originalError)) {
            try {
                this.oAuthConnectionVerifier.verifyOAuthConnection(link);
            }
            catch (Exception e) {
                return e;
            }
        }
        return originalError;
    }

    private DefaultApplinkStatus createStatus(ApplicationId id, ApplicationLink link, ApplinkOAuthStatus localStatus, ApplinkOAuthStatus remoteStatus, Exception error) {
        if (error instanceof ApplinkError) {
            ApplinkError applinkError = (ApplinkError)((Object)error);
            switch (applinkError.getType().getCategory()) {
                case DISABLED: {
                    return DefaultApplinkStatus.disabled(link, applinkError);
                }
                case CONFIG_ERROR: {
                    return DefaultApplinkStatus.configError(link, localStatus, remoteStatus, applinkError);
                }
                case NETWORK_ERROR: {
                    this.logApplinkError("Network", error, id);
                }
            }
            return DefaultApplinkStatus.error(link, localStatus, remoteStatus, applinkError);
        }
        this.logApplinkError("Unrecognized", error, id);
        return DefaultApplinkStatus.unknown(link, localStatus, error);
    }

    private void logApplinkError(String errorType, Exception error, ApplicationId id) {
        log.warn("{} error while attempting to retrieve status of Application Link '{}'", (Object)errorType, (Object)id);
        if (error instanceof ApplinkError) {
            ((ApplinkError)ApplinkError.class.cast(error)).accept(new ApplinkErrorLogger(id));
        }
        log.debug("{} error trace for '{}'", new Object[]{errorType, id, error});
    }

    private static final class ApplinkErrorLogger
    implements ApplinkErrorVisitor<Void> {
        private final ApplicationId id;

        private ApplinkErrorLogger(ApplicationId id) {
            this.id = id;
        }

        @Override
        @Nullable
        public Void visit(@Nonnull ApplinkError error) {
            log.debug("'{}' error type: {}", (Object)this.id, (Object)error.getType());
            return null;
        }

        @Override
        @Nullable
        public Void visit(@Nonnull AuthorisationUriAwareApplinkError error) {
            this.visit((ApplinkError)error);
            return null;
        }

        @Override
        @Nullable
        public Void visit(@Nonnull ResponseApplinkError responseError) {
            this.visit((ApplinkError)responseError);
            log.debug("'{}' response status: {}", (Object)this.id, (Object)HttpUtils.toStatusString(responseError.getStatusCode()));
            if (responseError.getBody() != null) {
                log.debug("'{}' response content type: {}", (Object)this.id, (Object)responseError.getContentType());
                log.debug("'{}' response contents\n\n{}\n\n", (Object)this.id, (Object)responseError.getBody());
            } else {
                log.debug("'{}' response has no body", (Object)this.id);
            }
            return null;
        }
    }
}

