/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableSetMultimap
 *  com.google.common.collect.ImmutableSetMultimap$Builder
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.config;

import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.IdpSearchParameters;
import com.atlassian.plugins.authentication.api.config.ImmutableSsoConfig;
import com.atlassian.plugins.authentication.api.config.SsoConfig;
import com.atlassian.plugins.authentication.api.config.SsoType;
import com.atlassian.plugins.authentication.api.config.ValidationError;
import com.atlassian.plugins.authentication.api.config.oidc.OidcConfig;
import com.atlassian.plugins.authentication.api.config.saml.SamlConfig;
import com.atlassian.plugins.authentication.api.exception.CannotDisableIdpException;
import com.atlassian.plugins.authentication.api.exception.InvalidConfigException;
import com.atlassian.plugins.authentication.impl.config.IdpConfigValidatorProvider;
import com.atlassian.plugins.authentication.impl.config.SsoConfigDao;
import com.atlassian.plugins.authentication.impl.config.ValidationContext;
import com.atlassian.plugins.authentication.impl.util.ApplicationStateValidator;
import com.atlassian.plugins.authentication.impl.util.ProductLicenseDataProvider;
import com.atlassian.plugins.authentication.impl.web.oidc.OidcDiscoveryException;
import com.atlassian.plugins.authentication.impl.web.oidc.OidcDiscoverySupport;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdpConfigServiceImpl
implements IdpConfigService {
    private static final Logger logger = LoggerFactory.getLogger(IdpConfigServiceImpl.class);
    private final SsoConfigDao ssoConfigDao;
    private final IdpConfigValidatorProvider idpConfigValidatorProvider;
    private final OidcDiscoverySupport oidcDiscoverySupport;
    private final ProductLicenseDataProvider productLicenseDataProvider;
    private final ApplicationStateValidator applicationStateValidator;

    public IdpConfigServiceImpl(SsoConfigDao ssoConfigDao, IdpConfigValidatorProvider idpConfigValidatorProvider, OidcDiscoverySupport oidcDiscoverySupport, ProductLicenseDataProvider productLicenseDataProvider, ApplicationStateValidator applicationStateValidator) {
        this.ssoConfigDao = ssoConfigDao;
        this.idpConfigValidatorProvider = idpConfigValidatorProvider;
        this.oidcDiscoverySupport = oidcDiscoverySupport;
        this.productLicenseDataProvider = productLicenseDataProvider;
        this.applicationStateValidator = applicationStateValidator;
    }

    @Override
    public List<IdpConfig> getIdpConfigs() {
        return this.ssoConfigDao.getIdpConfigs();
    }

    @Override
    public List<IdpConfig> getIdpConfigs(IdpSearchParameters searchParameters) {
        return this.ssoConfigDao.getIdpConfigs(searchParameters);
    }

    @Override
    public IdpConfig getIdpConfig(Long id) {
        return this.ssoConfigDao.findById(id);
    }

    @Override
    public IdpConfig updateIdpConfig(@Nonnull IdpConfig newConfig) {
        Objects.requireNonNull(newConfig, "IdP configuration cannot be null");
        Objects.requireNonNull(newConfig.getId(), "The id of the config to update must be specified");
        IdpConfig currentConfig = this.ssoConfigDao.findById(newConfig.getId());
        List<IdpConfig> idpConfigs = this.getIdpConfigs();
        if (currentConfig.isEnabled() && !newConfig.isEnabled()) {
            this.validateDisablingConfig(currentConfig, idpConfigs);
        }
        if (this.productLicenseDataProvider.isServiceManagementProduct() && currentConfig.isIncludeCustomerLogins() && !newConfig.isIncludeCustomerLogins()) {
            this.validateDisablingConfigForJsm(currentConfig, idpConfigs);
        }
        return this.updateIdpConfigInternal(currentConfig, this.refreshDiscoveryIfNeeded(newConfig), idpConfigs);
    }

    @Override
    public IdpConfig addIdpConfig(@Nonnull IdpConfig newConfig) {
        Objects.requireNonNull(newConfig, "IdP configuration cannot be null");
        return this.updateIdpConfigInternal(null, this.refreshDiscoveryIfNeeded(newConfig), this.getIdpConfigs());
    }

    @Override
    public IdpConfig removeIdpConfig(Long idpConfigId) {
        ImmutableSsoConfig newConfig;
        SsoConfig previousConfig = this.ssoConfigDao.getSsoConfig();
        ImmutableSsoConfig.Builder newConfigBuilder = ImmutableSsoConfig.toBuilder(previousConfig);
        if (this.isInsufficientNumberOfGlobalEnabledLoginOptions(idpConfigId)) {
            this.enableNativeLogin(newConfigBuilder);
        }
        if (this.isInsufficientNumberOfJsmEnabledLoginOptions(idpConfigId)) {
            this.enableNativeLoginForJsm(newConfigBuilder);
        }
        if (!previousConfig.equals(newConfig = newConfigBuilder.build())) {
            this.ssoConfigDao.saveSsoConfig(newConfig);
        }
        return this.ssoConfigDao.removeIdpConfig(idpConfigId);
    }

    @Override
    public IdpConfig refreshIdpConfig(IdpConfig configToRefresh) {
        return this.updateIdpConfigInternal(configToRefresh, this.refreshDiscoveryIfNeeded(configToRefresh), this.getIdpConfigs());
    }

    private void validateDisablingConfigForJsm(IdpConfig currentConfig, List<IdpConfig> idpConfigs) {
        List<IdpConfig> jsmEnabledConfigs = idpConfigs.stream().filter(IdpConfig::isIncludeCustomerLogins).collect(Collectors.toList());
        if (this.isInsufficientNumberOfJsmEnabledLoginOptions(currentConfig.getId(), jsmEnabledConfigs)) {
            throw new CannotDisableIdpException("Can't disable IDP for Jira Service Management.");
        }
    }

    private void validateDisablingConfig(IdpConfig currentConfig, List<IdpConfig> idpConfigs) {
        List<IdpConfig> enabledIdpConfigs = idpConfigs.stream().filter(IdpConfig::isEnabled).collect(Collectors.toList());
        if (this.isInsufficientNumberOfGlobalEnabledLoginOptions(currentConfig.getId(), enabledIdpConfigs)) {
            throw new CannotDisableIdpException("Can't disable IDP.");
        }
    }

    private boolean isInsufficientNumberOfGlobalEnabledLoginOptions(Long idpConfigId) {
        return this.isInsufficientNumberOfGlobalEnabledLoginOptions(idpConfigId, this.getIdpConfigs(IdpSearchParameters.allEnabled()));
    }

    private boolean isInsufficientNumberOfGlobalEnabledLoginOptions(Long idpConfigId, List<IdpConfig> enabledIdpConfigs) {
        return this.isInsufficientNumberOfEnabledLoginOptions(SsoConfig::getShowLoginForm, idpConfigId, enabledIdpConfigs);
    }

    private boolean isInsufficientNumberOfEnabledLoginOptions(Predicate<SsoConfig> nativeLoginFormEnabled, Long idpConfigId, List<IdpConfig> enabledIdpConfigs) {
        long numberOfLoginOptions = enabledIdpConfigs.stream().filter(c -> !Objects.equals(c.getId(), idpConfigId)).filter(this.applicationStateValidator::canProcessAuthenticationRequest).count();
        if (nativeLoginFormEnabled.test(this.ssoConfigDao.getSsoConfig())) {
            ++numberOfLoginOptions;
        }
        return numberOfLoginOptions < 1L;
    }

    private boolean isInsufficientNumberOfJsmEnabledLoginOptions(Long idpConfigId) {
        return this.isInsufficientNumberOfJsmEnabledLoginOptions(idpConfigId, this.getIdpConfigs(IdpSearchParameters.builder().setIncludeCustomerLoginsRestriction(true).build()));
    }

    private boolean isInsufficientNumberOfJsmEnabledLoginOptions(Long idpConfigId, List<IdpConfig> jsmEnabledConfigs) {
        return this.isInsufficientNumberOfEnabledLoginOptions(SsoConfig::getShowLoginFormForJsm, idpConfigId, jsmEnabledConfigs);
    }

    private void enableNativeLogin(ImmutableSsoConfig.Builder builder) {
        builder.setShowLoginForm(true);
    }

    private void enableNativeLoginForJsm(ImmutableSsoConfig.Builder builder) {
        builder.setShowLoginFormForJsm(true);
    }

    private IdpConfig updateIdpConfigInternal(@Nullable IdpConfig previousConfig, @Nonnull IdpConfig newConfig, List<IdpConfig> idpConfigs) {
        if (Objects.equals(newConfig, previousConfig)) {
            logger.debug("Skipping IdP config update as new config is identical to current config");
            return previousConfig;
        }
        Multimap<String, ValidationError> errorsOnFields = this.idpConfigValidatorProvider.getValidatorUnchecked(newConfig.getSsoType()).validate(newConfig);
        if (!errorsOnFields.isEmpty()) {
            throw new InvalidConfigException(errorsOnFields);
        }
        this.validateUniqueFields(newConfig, idpConfigs);
        return this.ssoConfigDao.saveIdpConfig(newConfig);
    }

    private void validateUniqueFields(IdpConfig newConfig, List<IdpConfig> idpConfigs) {
        ImmutableMultimap errors;
        ImmutableSetMultimap.Builder errorsBuilder = ImmutableSetMultimap.builder();
        List otherIdpConfigs = idpConfigs.stream().filter(c -> !Objects.equals(c.getId(), newConfig.getId())).collect(Collectors.toList());
        if (otherIdpConfigs.stream().anyMatch(c -> Objects.equals(newConfig.getButtonText(), c.getButtonText()))) {
            errorsBuilder.put((Object)"button-text", (Object)ValidationError.nonUnique());
        }
        if (otherIdpConfigs.stream().anyMatch(c -> Objects.equals(newConfig.getName(), c.getName()))) {
            errorsBuilder.put((Object)"name", (Object)ValidationError.nonUnique());
        }
        if (otherIdpConfigs.stream().anyMatch(c -> Objects.equals(newConfig.getIssuer(), c.getIssuer()))) {
            errorsBuilder.put((Object)this.issuerField(newConfig), (Object)ValidationError.nonUnique());
        }
        if (!(errors = errorsBuilder.build()).isEmpty()) {
            throw new InvalidConfigException((Multimap<String, ValidationError>)errors);
        }
    }

    @NotNull
    @VisibleForTesting
    String issuerField(IdpConfig idpConfig) {
        switch (idpConfig.getSsoType()) {
            case SAML: {
                return SamlConfig.from(idpConfig).get().getIdpType() == SamlConfig.IdpType.CROWD ? "crowd-url" : "sso-issuer";
            }
            case OIDC: {
                return "issuer-url";
            }
        }
        throw new IllegalStateException("Unknown SSO type: " + (Object)((Object)idpConfig.getSsoType()));
    }

    private IdpConfig refreshDiscoveryIfNeeded(IdpConfig newConfig) {
        return OidcConfig.from(newConfig).map(nc -> {
            if (nc.isDiscoveryEnabled()) {
                logger.info("Performing IdP discovery with issuer {}", (Object)nc.getIssuer());
                try {
                    Multimap<String, ValidationError> errorsOnFields = this.idpConfigValidatorProvider.getValidatorUnchecked(SsoType.OIDC).validate((IdpConfig)nc, ValidationContext.OIDC_DISCOVERY);
                    if (!errorsOnFields.isEmpty()) {
                        throw new InvalidConfigException(errorsOnFields);
                    }
                    return this.oidcDiscoverySupport.refresh((OidcConfig)nc);
                }
                catch (OidcDiscoveryException e) {
                    logger.info("Failed fetching metadata from OIDC discovery, issuer: {}.", (Object)nc.getIssuer(), (Object)e);
                    throw new InvalidConfigException((Multimap<String, ValidationError>)ImmutableMultimap.of((Object)"discovery-enabled", (Object)ValidationError.incorrect()));
                }
            }
            return newConfig;
        }).orElse(newConfig);
    }
}

