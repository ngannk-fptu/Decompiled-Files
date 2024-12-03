/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.config;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.IdpSearchParameters;
import com.atlassian.plugins.authentication.api.config.SsoConfig;
import com.atlassian.plugins.authentication.api.config.SsoConfigService;
import com.atlassian.plugins.authentication.api.config.ValidationError;
import com.atlassian.plugins.authentication.api.exception.CannotDisableLoginFormException;
import com.atlassian.plugins.authentication.api.exception.InvalidConfigException;
import com.atlassian.plugins.authentication.event.LoginFormToggledEvent;
import com.atlassian.plugins.authentication.event.OidcDiscoveryRefreshCronUpdatedEvent;
import com.atlassian.plugins.authentication.impl.config.SsoConfigDao;
import com.atlassian.plugins.authentication.impl.config.SsoConfigValidator;
import com.atlassian.plugins.authentication.impl.util.ApplicationStateValidator;
import com.atlassian.plugins.authentication.impl.util.ProductLicenseDataProvider;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@ExportAsService(value={SsoConfigService.class})
public class SsoConfigServiceImpl
implements SsoConfigService {
    private static final Logger logger = LoggerFactory.getLogger(SsoConfigServiceImpl.class);
    private final EventPublisher eventPublisher;
    private final SsoConfigDao ssoConfigDao;
    private final SsoConfigValidator ssoConfigValidator;
    private final IdpConfigService idpConfigService;
    private final ProductLicenseDataProvider productLicenseDataProvider;
    private ApplicationStateValidator applicationStateValidator;

    @Inject
    public SsoConfigServiceImpl(@ComponentImport EventPublisher eventPublisher, SsoConfigDao ssoConfigDao, SsoConfigValidator ssoConfigValidator, IdpConfigService idpConfigService, ProductLicenseDataProvider productLicenseDataProvider, ApplicationStateValidator applicationStateValidator) {
        this.eventPublisher = eventPublisher;
        this.ssoConfigDao = ssoConfigDao;
        this.ssoConfigValidator = ssoConfigValidator;
        this.idpConfigService = idpConfigService;
        this.productLicenseDataProvider = productLicenseDataProvider;
        this.applicationStateValidator = applicationStateValidator;
    }

    @Override
    public SsoConfig getSsoConfig() {
        return this.ssoConfigDao.getSsoConfig();
    }

    @Override
    public SsoConfig updateSsoConfig(@Nonnull SsoConfig newConfig) {
        Objects.requireNonNull(newConfig, "SSO configuration cannot be null");
        SsoConfig currentConfig = this.ssoConfigDao.getSsoConfig();
        this.validateSufficientLoginOptionsAreEnabled(newConfig, currentConfig);
        this.validateSufficientLoginOptionsForJsmAreEnabled(newConfig, currentConfig);
        return this.updateSsoConfigInternal(currentConfig, newConfig);
    }

    private void validateSufficientLoginOptionsAreEnabled(@Nonnull SsoConfig newConfig, SsoConfig currentConfig) {
        if (currentConfig.getShowLoginForm() && !newConfig.getShowLoginForm()) {
            List enabledIdpConfigs = this.idpConfigService.getIdpConfigs(IdpSearchParameters.allEnabled()).stream().filter(this.applicationStateValidator::canProcessAuthenticationRequest).collect(Collectors.toList());
            if (enabledIdpConfigs.size() < 1) {
                throw new CannotDisableLoginFormException("Can't disable login form");
            }
        }
    }

    private void validateSufficientLoginOptionsForJsmAreEnabled(@Nonnull SsoConfig newConfig, SsoConfig currentConfig) {
        List<IdpConfig> enabledIdpConfigs;
        if (this.productLicenseDataProvider.isServiceManagementProduct() && currentConfig.getShowLoginFormForJsm() && !newConfig.getShowLoginFormForJsm() && (enabledIdpConfigs = this.idpConfigService.getIdpConfigs(IdpSearchParameters.builder().setIncludeCustomerLoginsRestriction(true).build())).size() < 1) {
            throw new CannotDisableLoginFormException("Can't disable login form for Jira Service Management");
        }
    }

    private SsoConfig updateSsoConfigInternal(@Nullable SsoConfig previousConfig, @Nonnull SsoConfig newConfig) {
        if (Objects.equals(newConfig, previousConfig)) {
            return previousConfig;
        }
        Multimap<String, ValidationError> errorsOnFields = this.ssoConfigValidator.validate(newConfig);
        if (!errorsOnFields.isEmpty()) {
            throw new InvalidConfigException(errorsOnFields);
        }
        this.publishEvents(previousConfig, newConfig);
        return this.ssoConfigDao.saveSsoConfig(newConfig);
    }

    private void publishEvents(SsoConfig previousConfig, SsoConfig newConfig) {
        if (previousConfig == null || previousConfig.getShowLoginForm() != newConfig.getShowLoginForm()) {
            this.eventPublisher.publish((Object)new LoginFormToggledEvent(newConfig.getShowLoginForm()));
        }
        if (previousConfig == null || !Objects.equals(previousConfig.getDiscoveryRefreshCron(), newConfig.getDiscoveryRefreshCron())) {
            this.eventPublisher.publish((Object)new OidcDiscoveryRefreshCronUpdatedEvent());
        }
    }

    @Override
    public void resetConfig() {
        this.ssoConfigDao.removeSsoConfig();
        this.eventPublisher.publish((Object)new LoginFormToggledEvent(true));
    }
}

