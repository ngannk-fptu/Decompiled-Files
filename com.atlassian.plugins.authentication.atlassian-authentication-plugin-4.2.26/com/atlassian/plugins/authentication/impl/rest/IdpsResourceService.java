/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.rest;

import com.atlassian.plugins.authentication.api.config.AbstractIdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.IdpSearchParameters;
import com.atlassian.plugins.authentication.api.config.ImmutableJustInTimeConfig;
import com.atlassian.plugins.authentication.api.config.JustInTimeConfig;
import com.atlassian.plugins.authentication.api.config.SsoType;
import com.atlassian.plugins.authentication.api.config.oidc.OidcConfig;
import com.atlassian.plugins.authentication.api.config.saml.SamlConfig;
import com.atlassian.plugins.authentication.impl.rest.model.IdpConfigEntity;
import com.atlassian.plugins.authentication.impl.rest.model.RestPageRequest;
import com.atlassian.plugins.authentication.impl.util.ApplicationStateValidator;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class IdpsResourceService {
    private static final Logger log = LoggerFactory.getLogger(IdpsResourceService.class);
    private final IdpConfigService idpConfigService;
    private final ApplicationStateValidator applicationStateValidator;

    @Inject
    public IdpsResourceService(IdpConfigService idpConfigService, ApplicationStateValidator applicationStateValidator) {
        this.idpConfigService = idpConfigService;
        this.applicationStateValidator = applicationStateValidator;
    }

    @Nonnull
    public List<IdpConfigEntity> getConfigs(RestPageRequest pageRequest) {
        return this.idpConfigService.getIdpConfigs(IdpSearchParameters.builder().setPageParameters(pageRequest.toPageParameters()).build()).stream().map(IdpConfigEntity::new).collect(Collectors.toList());
    }

    public IdpConfigEntity getConfig(Long id) {
        Preconditions.checkNotNull((Object)id, (Object)"Id must not be null");
        return new IdpConfigEntity(this.idpConfigService.getIdpConfig(id));
    }

    @Nonnull
    public IdpConfigEntity addConfig(@Nonnull IdpConfigEntity entity) {
        IdpConfig configToStore = this.buildConfig(null, entity);
        this.applicationStateValidator.checkSsoIsAllowed(configToStore);
        return new IdpConfigEntity(this.idpConfigService.addIdpConfig(configToStore));
    }

    @Nonnull
    public IdpConfigEntity updateConfig(Long id, @Nonnull IdpConfigEntity entity) {
        IdpConfig configToStore = this.buildConfig(id, entity);
        if (configToStore.isEnabled()) {
            this.applicationStateValidator.checkSsoIsAllowed(configToStore);
        }
        return new IdpConfigEntity(this.idpConfigService.updateIdpConfig(configToStore));
    }

    public IdpConfigEntity removeConfig(Long id) {
        return new IdpConfigEntity(this.idpConfigService.removeIdpConfig(id));
    }

    private IdpConfig buildConfig(Long id, IdpConfigEntity entity) {
        IdpConfig currentConfig;
        if (id == null) {
            log.trace("No id supplied in the request, creating new IdP");
            currentConfig = null;
        } else {
            log.trace("Id [{}] provided in the request, fetching IdP with that id", (Object)id);
            currentConfig = this.idpConfigService.getIdpConfig(id);
        }
        SsoType currentSsoType = currentConfig == null ? null : currentConfig.getSsoType();
        SsoType ssoType = entity.getSsoType() == null ? currentSsoType : entity.getSsoType();
        Preconditions.checkArgument((ssoType != null ? 1 : 0) != 0, (Object)"Field sso-type is required");
        switch (ssoType) {
            case SAML: {
                return this.buildSamlConfig(entity, SamlConfig.from(currentConfig));
            }
            case OIDC: {
                return this.buildOidcConfig(entity, OidcConfig.from(currentConfig));
            }
        }
        throw new IllegalArgumentException(String.format("Unknown SSO type: %s", new Object[]{entity.getSsoType()}));
    }

    private SamlConfig buildSamlConfig(@Nonnull IdpConfigEntity entity, Optional<SamlConfig> currentConfig) {
        this.validateMutuallyExclusiveFields("crowd-url", entity.getCrowdUrl(), "sso-url", entity.getSsoUrl());
        this.validateMutuallyExclusiveFields("crowd-url", entity.getCrowdUrl(), "sso-issuer", entity.getSsoIssuer());
        SamlConfig.Builder builder = currentConfig.map(SamlConfig::toBuilder).orElseGet(SamlConfig::builder);
        this.updateGenericConfig(currentConfig.orElse(null), builder, entity);
        SamlConfig.IdpType idpType = this.calculateSamlIdpType(entity, currentConfig.orElse(null)).orElse(SamlConfig.IdpType.GENERIC);
        builder.setIdpType(idpType);
        if (idpType == SamlConfig.IdpType.CROWD) {
            this.setIfNonNull(entity.getCrowdUrl(), builder::setCrowdBaseUrl);
        } else {
            this.setIfNonNull(entity.getSsoUrl(), builder::setSsoUrl);
            this.setIfNonNull(entity.getSsoIssuer(), builder::setIssuer);
        }
        this.setIfNonNull(entity.getCertificate(), builder::setCertificate);
        this.setIfNonNull(entity.getUserAttribute(), builder::setUsernameAttribute);
        return builder.build();
    }

    private OidcConfig buildOidcConfig(@Nonnull IdpConfigEntity entity, Optional<OidcConfig> currentConfig) {
        OidcConfig.Builder builder = currentConfig.map(OidcConfig::toBuilder).orElseGet(OidcConfig::builder);
        this.updateGenericConfig(currentConfig.orElse(null), builder, entity);
        this.setIfNonNull(entity.getIssuerUrl(), builder::setIssuer);
        this.setIfNonNull(entity.getClientId(), builder::setClientId);
        this.setIfNonNull(entity.getClientSecret(), builder::setClientSecret);
        this.setIfNonNull(entity.getDiscoveryEnabled(), builder::setDiscoveryEnabled);
        if (!Boolean.TRUE.equals(entity.getDiscoveryEnabled())) {
            this.setIfNonNull(entity.getAuthorizationEndpoint(), builder::setAuthorizationEndpoint);
            this.setIfNonNull(entity.getTokenEndpoint(), builder::setTokenEndpoint);
            this.setIfNonNull(entity.getUserInfoEndpoint(), builder::setUserInfoEndpoint);
        }
        this.setIfNonNull(entity.getAdditionalScopes(), scopes -> builder.setAdditionalScopes(Iterables.filter((Iterable)scopes, Objects::nonNull)));
        this.setIfNonNull(entity.getUsernameClaim(), builder::setUsernameClaim);
        return builder.build();
    }

    private void updateGenericConfig(@Nullable IdpConfig currentConfig, @Nonnull AbstractIdpConfig.Builder<?> builder, @Nonnull IdpConfigEntity entity) {
        this.setIfNonNull(entity.getId(), builder::setId);
        this.setIfNonNull(entity.getName(), builder::setName);
        this.setIfNonNull(entity.getEnabled(), builder::setEnabled);
        this.setIfNonNull(entity.getIncludeCustomerLogins(), builder::setIncludeCustomerLogins);
        this.setIfNonNull(entity.getEnableRememberMe(), builder::setEnableRememberMe);
        this.setIfNonNull(entity.getButtonText(), builder::setButtonText);
        this.updateJustInTimeConfig(Optional.ofNullable(currentConfig).map(IdpConfig::getJustInTimeConfig).orElse(null), builder, entity);
    }

    private void updateJustInTimeConfig(@Nullable JustInTimeConfig currentJustInTimeConfig, @Nonnull AbstractIdpConfig.Builder<?> builder, IdpConfigEntity entity) {
        ImmutableJustInTimeConfig.Builder jitBuilder = ImmutableJustInTimeConfig.builder(currentJustInTimeConfig);
        if (entity.getJitConfiguration() != null) {
            this.setIfNonNull(entity.getJitConfiguration().getEnableUserProvisioning(), jitBuilder::setEnabled);
            this.setIfNonNull(entity.getJitConfiguration().getMappingDisplayName(), jitBuilder::setDisplayNameMappingExpression);
            this.setIfNonNull(entity.getJitConfiguration().getMappingEmail(), jitBuilder::setEmailMappingExpression);
            this.setIfNonNull(entity.getJitConfiguration().getMappingGroups(), jitBuilder::setGroupsMappingSource);
            this.setIfNonNull(entity.getJitConfiguration().getAdditionalJitScopes(), scopes -> jitBuilder.setAdditionalJitScopes(Iterables.filter((Iterable)scopes, Objects::nonNull)));
        }
        builder.setJustInTimeConfig(jitBuilder.build());
    }

    @Nonnull
    private Optional<SamlConfig.IdpType> calculateSamlIdpType(@Nonnull IdpConfigEntity entity, @Nullable SamlConfig currentConfig) {
        SamlConfig.IdpType idpType = null;
        if (entity.getIdpType() != null) {
            log.debug("IdP type specified in the request: {}", (Object)entity.getIdpType());
            idpType = entity.getIdpType();
        } else if (entity.getCrowdUrl() != null) {
            log.debug("Crowd URL [{}] specified in the request, treating IdP as Crowd", (Object)entity.getCrowdUrl());
            idpType = SamlConfig.IdpType.CROWD;
        } else if (entity.getSsoUrl() != null || entity.getSsoIssuer() != null) {
            log.debug("SSO URL [{}] or issuer [{}] is not null, treating IdP as generic", (Object)entity.getSsoUrl(), (Object)entity.getSsoIssuer());
            idpType = SamlConfig.IdpType.GENERIC;
        } else if (currentConfig != null) {
            log.debug("Not enough data in the request to determine IdP type, treating input as addition to current type of [{}]", (Object)currentConfig.getIdpType());
            idpType = currentConfig.getIdpType();
        }
        return Optional.ofNullable(idpType);
    }

    private <T> void setIfNonNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    private void validateMutuallyExclusiveFields(@Nonnull String field1Name, @Nullable Object field1Value, @Nonnull String field2Name, @Nullable Object field2Value) {
        if (field1Value != null && field2Value != null) {
            throw new IllegalArgumentException(String.format("Either '%s' or '%s' must be set, received both", field1Name, field2Name));
        }
    }
}

