/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  javax.inject.Inject
 *  org.jetbrains.annotations.NotNull
 *  org.springframework.stereotype.Component
 */
package com.atlassian.plugins.authentication.upgrade;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={PluginUpgradeTask.class})
public class UpgradeTask03ExtractSsoTypeFromIdpType
implements PluginUpgradeTask {
    @VisibleForTesting
    static final String CFG_PREFIX = "com.atlassian.plugins.authentication.sso.config.";
    @VisibleForTesting
    static final String CFG_IDP_TYPE = "idp-type";
    @VisibleForTesting
    static final String CFG_SSO_TYPE = "sso-type";
    @VisibleForTesting
    static final String SSO_TYPE_NONE = "NONE";
    @VisibleForTesting
    static final String SSO_TYPE_SAML = "SAML";
    @VisibleForTesting
    static final String IDP_TYPE_NONE = "NONE";
    @VisibleForTesting
    static final String IDP_TYPE_GENERIC = "GENERIC";
    @VisibleForTesting
    static final String IDP_TYPE_CROWD = "CROWD";
    @VisibleForTesting
    static final String SAML_SPECIFIC_FIELD = "sso-issuer";
    private static final Map<String, String> IDP_TYPE_TO_SSO_TYPE = ImmutableMap.of((Object)"NONE", (Object)"NONE", (Object)"GENERIC", (Object)"SAML", (Object)"CROWD", (Object)"SAML");
    private final PluginSettingsFactory pluginSettings;

    @Inject
    public UpgradeTask03ExtractSsoTypeFromIdpType(@ComponentImport PluginSettingsFactory pluginSettings) {
        this.pluginSettings = pluginSettings;
    }

    public int getBuildNumber() {
        return 3;
    }

    public String getShortDescription() {
        return "Extract SSO type ('NONE', 'SAML') based on IDP type ('NONE', 'GENERIC', 'CROWD') or based on saved properties before 3.2.0v";
    }

    public Collection<Message> doUpgrade() throws Exception {
        PluginSettings globalSettings = this.pluginSettings.createGlobalSettings();
        String idpType = Optional.ofNullable(globalSettings.get("com.atlassian.plugins.authentication.sso.config.idp-type")).map(Object::toString).orElseGet(() -> this.inferIdpType(globalSettings));
        String ssoType = Optional.ofNullable(globalSettings.get("com.atlassian.plugins.authentication.sso.config.sso-type")).map(Object::toString).orElseGet(() -> IDP_TYPE_TO_SSO_TYPE.getOrDefault(idpType, "NONE"));
        globalSettings.put("com.atlassian.plugins.authentication.sso.config.sso-type", (Object)ssoType);
        if ("NONE".equals(ssoType) || "NONE".equals(idpType)) {
            globalSettings.remove("com.atlassian.plugins.authentication.sso.config.idp-type");
        }
        return Collections.emptyList();
    }

    @NotNull
    private String inferIdpType(PluginSettings globalSettings) {
        if (globalSettings.get("com.atlassian.plugins.authentication.sso.config.sso-issuer") != null) {
            globalSettings.put("com.atlassian.plugins.authentication.sso.config.idp-type", (Object)IDP_TYPE_GENERIC);
            return IDP_TYPE_GENERIC;
        }
        return "NONE";
    }

    public String getPluginKey() {
        return "com.atlassian.plugins.authentication.atlassian-authentication-plugin";
    }
}

