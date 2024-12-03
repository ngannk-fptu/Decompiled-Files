/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  javax.inject.Inject
 *  javax.inject.Named
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.config;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpSearchParameters;
import com.atlassian.plugins.authentication.api.config.ImmutableJustInTimeConfig;
import com.atlassian.plugins.authentication.api.config.ImmutableSsoConfig;
import com.atlassian.plugins.authentication.api.config.JustInTimeConfig;
import com.atlassian.plugins.authentication.api.config.PageParameters;
import com.atlassian.plugins.authentication.api.config.SsoConfig;
import com.atlassian.plugins.authentication.api.config.SsoType;
import com.atlassian.plugins.authentication.api.config.oidc.OidcConfig;
import com.atlassian.plugins.authentication.api.config.saml.SamlConfig;
import com.atlassian.plugins.authentication.impl.config.IdpConfigEntity;
import com.atlassian.plugins.authentication.impl.config.IdpNotFoundException;
import com.atlassian.plugins.authentication.impl.config.PluginSettingsUtil;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class SsoConfigDao {
    private static final Logger logger = LoggerFactory.getLogger(SsoConfigDao.class);
    public static final String CFG_PREFIX = "com.atlassian.plugins.authentication.sso.config.";
    private final PluginSettingsFactory pluginSettings;
    private final ActiveObjects activeObjects;
    private final TimeZoneManager timeZoneManager;
    private final Clock clock;

    @Inject
    public SsoConfigDao(@ComponentImport PluginSettingsFactory pluginSettings, @ComponentImport ActiveObjects activeObjects, @ComponentImport TimeZoneManager timeZoneManager, Clock clock) {
        this.pluginSettings = pluginSettings;
        this.activeObjects = activeObjects;
        this.timeZoneManager = timeZoneManager;
        this.clock = clock;
    }

    public List<IdpConfig> getIdpConfigs() {
        return this.getIdpConfigs(IdpSearchParameters.builder().build());
    }

    public List<IdpConfig> getIdpConfigs(IdpSearchParameters searchParameters) {
        List<IdpConfig> idpConfigs = Arrays.stream(this.activeObjects.find(IdpConfigEntity.class, this.buildIdpQuery(searchParameters))).map(this::mapIdpConfig).collect(Collectors.toList());
        logger.debug("Returning {} IdP configs", (Object)idpConfigs.size());
        return idpConfigs;
    }

    public SsoConfig getSsoConfig() {
        return this.readGenericSsoConfig(this.settings());
    }

    public void removeSsoConfig() {
        PluginSettings settings = this.settings();
        Stream.of("show-login-form", "enable-authentication-fallback", "show-login-form-for-jsm", "last-updated", "discovery-refresh-cron").forEach(config -> PluginSettingsUtil.removeValue(settings, config));
    }

    public IdpConfig removeIdpConfig(Long idpConfigId) {
        logger.debug("Deleting IdP config with id {}", (Object)idpConfigId);
        IdpConfigEntity idpConfigEntity = this.findByIdInternal(idpConfigId);
        IdpConfig idpConfig = this.mapIdpConfig(idpConfigEntity);
        this.activeObjects.delete(new RawEntity[]{idpConfigEntity});
        logger.debug("Deleted IdP config with id {}", (Object)idpConfigId);
        return idpConfig;
    }

    public SsoConfig saveSsoConfig(@Nonnull SsoConfig ssoConfig) {
        PluginSettings settings = this.settings();
        this.saveGenericSsoConfig(settings, ssoConfig);
        return this.readGenericSsoConfig(settings);
    }

    public IdpConfig saveIdpConfig(@Nonnull IdpConfig idpConfig) {
        IdpConfigEntity configToUpdate = idpConfig.getId() != null ? this.findByIdInternal(idpConfig.getId()) : (IdpConfigEntity)this.activeObjects.create(IdpConfigEntity.class, this.buildRequiredFieldMap(idpConfig));
        switch (idpConfig.getSsoType()) {
            case SAML: {
                SamlConfig samlConfig = (SamlConfig)idpConfig;
                return this.saveSamlConfig(configToUpdate, samlConfig);
            }
            case OIDC: {
                OidcConfig oidcConfig = (OidcConfig)idpConfig;
                return this.saveOidcConfig(configToUpdate, oidcConfig);
            }
        }
        throw new IllegalArgumentException("Unknown SSO type: " + (Object)((Object)idpConfig.getSsoType()));
    }

    public IdpConfig findById(Long id) {
        IdpConfigEntity idpConfig = this.findByIdInternal(id);
        return this.mapIdpConfig(idpConfig);
    }

    @Nonnull
    private IdpConfig mapIdpConfig(IdpConfigEntity idpConfig) {
        SsoType ssoType = SsoType.fromName(idpConfig.getSsoType()).orElseThrow(() -> new IllegalStateException("Unknown SSO type: " + idpConfig.getSsoType()));
        switch (ssoType) {
            case SAML: {
                return ((SamlConfig.Builder)((SamlConfig.Builder)((SamlConfig.Builder)((SamlConfig.Builder)((SamlConfig.Builder)((SamlConfig.Builder)((SamlConfig.Builder)((SamlConfig.Builder)((SamlConfig.Builder)((SamlConfig.Builder)SamlConfig.builder().setId(idpConfig.getID())).setName(idpConfig.getName())).setEnabled(idpConfig.isEnabled())).setJustInTimeConfig(this.readJustInTimeConfig(idpConfig))).setIdpType(SamlConfig.IdpType.fromName(idpConfig.getIdpType()).orElse(SamlConfig.IdpType.GENERIC)).setEnableRememberMe(idpConfig.isEnableRememberMe())).setIncludeCustomerLogins(idpConfig.isIncludeCustomerLogins())).setButtonText(idpConfig.getButtonText())).setSsoUrl(idpConfig.getSsoUrl()).setIssuer(idpConfig.getIssuer())).setCertificate(idpConfig.getCertificate()).setUsernameAttribute(idpConfig.getUserAttribute()).setIncludeCustomerLogins(idpConfig.isIncludeCustomerLogins())).setLastUpdated(idpConfig.getLastUpdated() == null ? null : ZonedDateTime.ofInstant(idpConfig.getLastUpdated().toInstant(), this.timeZoneManager.getDefaultTimeZone().toZoneId()))).build();
            }
            case OIDC: {
                return ((OidcConfig.Builder)((OidcConfig.Builder)((OidcConfig.Builder)((OidcConfig.Builder)((OidcConfig.Builder)((OidcConfig.Builder)((OidcConfig.Builder)((OidcConfig.Builder)((OidcConfig.Builder)OidcConfig.builder().setId(idpConfig.getID())).setName(idpConfig.getName())).setEnabled(idpConfig.isEnabled())).setJustInTimeConfig(this.readJustInTimeConfig(idpConfig)).setEnableRememberMe(idpConfig.isEnableRememberMe())).setIncludeCustomerLogins(idpConfig.isIncludeCustomerLogins())).setButtonText(idpConfig.getButtonText())).setIssuer(idpConfig.getIssuer())).setClientId(idpConfig.getClientId()).setClientSecret(idpConfig.getClientSecret()).setAuthorizationEndpoint(idpConfig.getAuthorizationEndpoint()).setTokenEndpoint(idpConfig.getTokenEndpoint()).setUserInfoEndpoint(idpConfig.getUserInfoEndpoint()).setDiscoveryEnabled(idpConfig.isUseDiscovery()).setIncludeCustomerLogins(idpConfig.isIncludeCustomerLogins())).setAdditionalScopes((Iterable)new Gson().fromJson(idpConfig.getAdditionalScopes(), new TypeToken<List<String>>(){}.getType())).setUsernameClaim(idpConfig.getUsernameClaim()).setLastUpdated(idpConfig.getLastUpdated() == null ? null : ZonedDateTime.ofInstant(idpConfig.getLastUpdated().toInstant(), this.timeZoneManager.getDefaultTimeZone().toZoneId()))).build();
            }
        }
        throw new IllegalStateException("Unknown type of SSO configured: " + idpConfig.getSsoType());
    }

    private SsoConfig readGenericSsoConfig(@Nonnull PluginSettings settings) {
        return ImmutableSsoConfig.builder().setShowLoginForm(PluginSettingsUtil.getBooleanValue(settings, "show-login-form", true)).setShowLoginFormForJsm(PluginSettingsUtil.getBooleanValue(settings, "show-login-form-for-jsm", false)).setEnableAuthenticationFallback(PluginSettingsUtil.getBooleanValue(settings, "enable-authentication-fallback", false)).setDiscoveryRefreshCron(PluginSettingsUtil.getStringValue(settings, "discovery-refresh-cron")).setLastUpdated(PluginSettingsUtil.getDateValue(settings, "last-updated", this.timeZoneManager.getDefaultTimeZone().toZoneId())).build();
    }

    @Nonnull
    private JustInTimeConfig readJustInTimeConfig(@Nonnull IdpConfigEntity idpConfigEntity) {
        ImmutableJustInTimeConfig.Builder jitConfig = ImmutableJustInTimeConfig.builder().setEnabled(idpConfigEntity.isUserProvisioningEnabled()).setDisplayNameMappingExpression(idpConfigEntity.getDisplayNameMapping()).setEmailMappingExpression(idpConfigEntity.getEmailMapping()).setGroupsMappingSource(idpConfigEntity.getGroupsMapping());
        if (idpConfigEntity.getSsoType().equals(SsoType.OIDC.name())) {
            jitConfig.setAdditionalJitScopes((Iterable)new Gson().fromJson(idpConfigEntity.getAdditionalJitScopes(), new TypeToken<List<String>>(){}.getType()));
        }
        return jitConfig.build();
    }

    @NotNull
    private IdpConfigEntity findByIdInternal(Long id) {
        IdpConfigEntity maybeIdpConfig = (IdpConfigEntity)this.activeObjects.get(IdpConfigEntity.class, (Object)id);
        if (maybeIdpConfig == null) {
            throw new IdpNotFoundException(id);
        }
        return maybeIdpConfig;
    }

    private ImmutableMap<String, Object> buildRequiredFieldMap(IdpConfig idpConfig) {
        return ImmutableMap.builder().put((Object)"ENABLED", (Object)idpConfig.isEnabled()).put((Object)"BUTTON_TEXT", (Object)idpConfig.getButtonText()).put((Object)"NAME", (Object)idpConfig.getName()).put((Object)"ISSUER", (Object)idpConfig.getIssuer()).build();
    }

    private IdpConfig saveOidcConfig(IdpConfigEntity aoConfig, OidcConfig oidcConfig) {
        this.mapOidcGeneralConfig(oidcConfig, aoConfig);
        this.mapOidcJitConfig(oidcConfig, aoConfig);
        aoConfig.save();
        logger.debug("Saved OIDC config with id [{}]", (Object)aoConfig.getID());
        return this.mapIdpConfig(aoConfig);
    }

    private void mapOidcJitConfig(OidcConfig oidcConfig, IdpConfigEntity aoConfig) {
        JustInTimeConfig jitConfig = oidcConfig.getJustInTimeConfig();
        aoConfig.setUserProvisioning(jitConfig.isEnabled().orElse(false));
        aoConfig.setDisplayNameMapping(jitConfig.getDisplayNameMappingExpression().orElse(""));
        aoConfig.setEmailMapping(jitConfig.getEmailMappingExpression().orElse(""));
        aoConfig.setGroupsMapping(jitConfig.getGroupsMappingSource().orElse(""));
        aoConfig.setAdditionalJitScopes(new Gson().toJson(jitConfig.getAdditionalJitScopes()));
    }

    private void mapOidcGeneralConfig(OidcConfig oidcConfig, IdpConfigEntity aoConfig) {
        aoConfig.setSsoType(SsoType.OIDC.name());
        aoConfig.setName(oidcConfig.getName());
        aoConfig.setEnabled(oidcConfig.isEnabled());
        aoConfig.setIssuer(oidcConfig.getIssuer());
        aoConfig.setClientId(oidcConfig.getClientId());
        aoConfig.setClientSecret(oidcConfig.getClientSecret());
        aoConfig.setAuthorizationEndpoint(oidcConfig.getAuthorizationEndpoint());
        aoConfig.setTokenEndpoint(oidcConfig.getTokenEndpoint());
        aoConfig.setUserInfoEndpoint(oidcConfig.getUserInfoEndpoint());
        aoConfig.setUseDiscovery(oidcConfig.isDiscoveryEnabled());
        aoConfig.setAdditionalScopes(new Gson().toJson(oidcConfig.getAdditionalScopes()));
        aoConfig.setUsernameClaim(Strings.emptyToNull((String)oidcConfig.getUsernameClaim()));
        aoConfig.setEnableRememberMe(oidcConfig.isEnableRememberMe());
        aoConfig.setIncludeCustomerLogins(oidcConfig.isIncludeCustomerLogins());
        aoConfig.setLastUpdated(Date.from(this.clock.instant()));
        aoConfig.setButtonText(oidcConfig.getButtonText());
    }

    private IdpConfig saveSamlConfig(IdpConfigEntity idpConfigEntity, SamlConfig samlConfig) {
        this.mapSamlGeneralConfig(samlConfig, idpConfigEntity);
        this.mapSamlJitConfig(samlConfig, idpConfigEntity);
        idpConfigEntity.save();
        logger.debug("Saved SAML config with id [{}]", (Object)idpConfigEntity.getID());
        return this.mapIdpConfig(idpConfigEntity);
    }

    private void mapSamlJitConfig(@Nonnull SamlConfig samlConfig, IdpConfigEntity idpConfigEntity) {
        JustInTimeConfig justInTimeConfig = samlConfig.getJustInTimeConfig();
        idpConfigEntity.setUserProvisioning(justInTimeConfig.isEnabled().orElse(false));
        idpConfigEntity.setDisplayNameMapping(justInTimeConfig.getDisplayNameMappingExpression().orElse(""));
        idpConfigEntity.setEmailMapping(justInTimeConfig.getEmailMappingExpression().orElse(""));
        idpConfigEntity.setGroupsMapping(justInTimeConfig.getGroupsMappingSource().orElse(""));
    }

    private void mapSamlGeneralConfig(SamlConfig samlConfig, IdpConfigEntity idpConfigEntity) {
        idpConfigEntity.setSsoType(SsoType.SAML.name());
        idpConfigEntity.setName(samlConfig.getName());
        idpConfigEntity.setEnabled(samlConfig.isEnabled());
        idpConfigEntity.setIdpType(samlConfig.getIdpType().name());
        idpConfigEntity.setSsoUrl(samlConfig.getSsoUrl());
        idpConfigEntity.setIssuer(samlConfig.getIssuer());
        idpConfigEntity.setCertificate(samlConfig.getCertificate());
        idpConfigEntity.setUserAttribute(samlConfig.getUsernameAttribute());
        idpConfigEntity.setEnableRememberMe(samlConfig.isEnableRememberMe());
        idpConfigEntity.setIncludeCustomerLogins(samlConfig.isIncludeCustomerLogins());
        idpConfigEntity.setLastUpdated(Date.from(this.clock.instant()));
        idpConfigEntity.setButtonText(samlConfig.getButtonText());
    }

    private void saveGenericSsoConfig(@Nonnull PluginSettings settings, @Nonnull SsoConfig ssoConfig) {
        PluginSettingsUtil.setBooleanValue(settings, "show-login-form", ssoConfig.getShowLoginForm());
        PluginSettingsUtil.setBooleanValue(settings, "show-login-form-for-jsm", ssoConfig.getShowLoginFormForJsm());
        PluginSettingsUtil.setBooleanValue(settings, "enable-authentication-fallback", ssoConfig.enableAuthenticationFallback());
        PluginSettingsUtil.setStringValue(settings, "discovery-refresh-cron", ssoConfig.getDiscoveryRefreshCron());
        PluginSettingsUtil.setDateValue(settings, "last-updated", this.clock.instant().atZone(this.timeZoneManager.getDefaultTimeZone().toZoneId()));
    }

    @Nonnull
    private PluginSettings settings() {
        return this.pluginSettings.createGlobalSettings();
    }

    private Query buildIdpQuery(IdpSearchParameters searchParameters) {
        PageParameters pageParameters = searchParameters.getPageParameters();
        Preconditions.checkArgument((pageParameters.isAllResultsQuery() || pageParameters.getLimit() > 0 && pageParameters.getStart() >= 0 ? 1 : 0) != 0, (Object)"The limit must be greater than zero and the start must be greater or equal to zero");
        Query query = Query.select().order("ID ASC");
        if (!pageParameters.isAllResultsQuery()) {
            query.offset(pageParameters.getStart()).limit(pageParameters.getLimit());
        }
        TreeMap where = new TreeMap();
        searchParameters.getEnabledRestriction().ifPresent(enabledFiltering -> where.put("ENABLED = ?", enabledFiltering));
        searchParameters.getSsoTypeRestriction().ifPresent(ssoType -> where.put("SSO_TYPE = ?", ssoType));
        searchParameters.getIncludeCustomerLoginsRestriction().ifPresent(includeCustomerLogins -> where.put("INCLUDE_CUSTOMER_LOGINS = ?", includeCustomerLogins));
        if (!where.isEmpty()) {
            query.where(String.join((CharSequence)" AND ", where.keySet()), where.values().toArray());
        }
        logger.trace("IdP query parameters - start {}, limit: {}, where: {}", new Object[]{query.getOffset(), query.getLimit(), query.getWhereClause()});
        return query;
    }

    public static interface Config {
        public static final String SHOW_LOGIN_FORM = "show-login-form";
        public static final String SHOW_LOGIN_FORM_FOR_JSM = "show-login-form-for-jsm";
        public static final String ENABLE_AUTHENTICATION_FALLBACK = "enable-authentication-fallback";
        public static final String LAST_UPDATED = "last-updated";

        public static interface Oidc {
            public static final String DISCOVERY_REFRESH_CRON = "discovery-refresh-cron";
        }
    }
}

