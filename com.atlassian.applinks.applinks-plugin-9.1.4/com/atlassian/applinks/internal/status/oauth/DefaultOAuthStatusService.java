/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.status.oauth;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.applink.ApplinkHelper;
import com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService;
import com.atlassian.applinks.internal.common.auth.oauth.OAuthConfigurator;
import com.atlassian.applinks.internal.common.auth.oauth.ServiceProviderStoreService;
import com.atlassian.applinks.internal.common.exception.ConsumerInformationUnavailableException;
import com.atlassian.applinks.internal.common.exception.NoAccessException;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.applinks.internal.common.exception.ServiceExceptionFactory;
import com.atlassian.applinks.internal.common.i18n.I18nKey;
import com.atlassian.applinks.internal.permission.PermissionValidationService;
import com.atlassian.applinks.internal.status.oauth.ApplinkOAuthStatus;
import com.atlassian.applinks.internal.status.oauth.OAuthStatusService;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultOAuthStatusService
implements OAuthStatusService {
    private static final I18nKey TWOLOI_OPERATION_KEY = I18nKey.newI18nKey("applinks.service.permission.operation.twoloi", new Serializable[0]);
    private static final I18nKey CHANGE_OAUTH_OPERATION_KEY = I18nKey.newI18nKey("applinks.service.permission.operation.changeoauth", new Serializable[0]);
    private final ApplinkHelper applinkHelper;
    private final PermissionValidationService permissionValidationService;
    private final OAuthConfigurator oAuthConfigurator;

    @Autowired
    public DefaultOAuthStatusService(ApplinkHelper applinkHelper, AuthenticationConfigurationManager authenticationConfigurationManager, ConsumerTokenStoreService consumerTokenStoreService, PermissionValidationService permissionValidationService, ServiceProviderStoreService serviceProviderStoreService, ServiceExceptionFactory serviceExceptionFactory) {
        this.applinkHelper = applinkHelper;
        this.permissionValidationService = permissionValidationService;
        this.oAuthConfigurator = new OAuthConfigurator(authenticationConfigurationManager, consumerTokenStoreService, serviceProviderStoreService, serviceExceptionFactory);
    }

    @VisibleForTesting
    public DefaultOAuthStatusService(ApplinkHelper applinkHelper, PermissionValidationService permissionValidationService, OAuthConfigurator oAuthConfigurator) {
        this.applinkHelper = applinkHelper;
        this.permissionValidationService = permissionValidationService;
        this.oAuthConfigurator = oAuthConfigurator;
    }

    @Override
    @Nonnull
    public ApplinkOAuthStatus getOAuthStatus(@Nonnull ApplicationId id) throws NoSuchApplinkException {
        return this.getOAuthStatus(this.applinkHelper.getApplicationLink(id));
    }

    @Override
    @Nonnull
    public ApplinkOAuthStatus getOAuthStatus(@Nonnull ApplicationLink link) {
        return new ApplinkOAuthStatus(this.oAuthConfigurator.getIncomingConfig(link), this.oAuthConfigurator.getOutgoingConfig(link));
    }

    @Override
    public void updateOAuthStatus(@Nonnull ApplicationId id, @Nonnull ApplinkOAuthStatus status) throws NoSuchApplinkException, NoAccessException, ConsumerInformationUnavailableException {
        this.updateOAuthStatus(this.applinkHelper.getApplicationLink(id), status);
    }

    @Override
    public void updateOAuthStatus(@Nonnull ApplicationLink applink, @Nonnull ApplinkOAuthStatus status) throws NoAccessException, ConsumerInformationUnavailableException {
        this.validatePermissions(status);
        this.oAuthConfigurator.updateIncomingConfig(applink, status.getIncoming());
        this.oAuthConfigurator.updateOutgoingConfig(applink, status.getOutgoing());
    }

    private void validatePermissions(ApplinkOAuthStatus status) throws NoAccessException {
        if (status.getIncoming().isTwoLoImpersonationEnabled() || status.getOutgoing().isTwoLoImpersonationEnabled()) {
            this.permissionValidationService.validateSysadmin(TWOLOI_OPERATION_KEY);
        } else {
            this.permissionValidationService.validateAdmin(CHANGE_OAUTH_OPERATION_KEY);
        }
    }
}

