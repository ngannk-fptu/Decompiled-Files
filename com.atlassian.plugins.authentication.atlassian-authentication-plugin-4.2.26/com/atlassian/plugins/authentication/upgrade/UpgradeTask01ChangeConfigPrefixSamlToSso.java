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
 *  com.google.common.collect.ImmutableList
 *  javax.inject.Inject
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
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={PluginUpgradeTask.class})
public class UpgradeTask01ChangeConfigPrefixSamlToSso
implements PluginUpgradeTask {
    @VisibleForTesting
    static final String OLD_CFG_PREFIX = "com.atlassian.plugins.authentication.samlconfig.";
    @VisibleForTesting
    static final String NEW_CFG_PREFIX = "com.atlassian.plugins.authentication.sso.config.";
    @VisibleForTesting
    static final List<String> ALL_CONFIG_FIELDS = ImmutableList.of((Object)"idp-type", (Object)"redirect-on-login", (Object)"allow-saml-override", (Object)"include-customer-logins", (Object)"enable-remember-me", (Object)"sso-url", (Object)"sso-issuer", (Object)"signing-cert", (Object)"username-attribute", (Object)"jira.login.gadget.initially.disabled", (Object)"bamboo.signup.initially.disabled");
    private final PluginSettingsFactory pluginSettings;

    @Inject
    public UpgradeTask01ChangeConfigPrefixSamlToSso(@ComponentImport PluginSettingsFactory pluginSettings) {
        this.pluginSettings = pluginSettings;
    }

    public int getBuildNumber() {
        return 1;
    }

    public String getShortDescription() {
        return "Change config prefix from 'SAML' to more generic 'SSO', to allow future non-SAML protocols";
    }

    public Collection<Message> doUpgrade() throws Exception {
        PluginSettings globalSettings = this.pluginSettings.createGlobalSettings();
        for (String field : ALL_CONFIG_FIELDS) {
            Object value = globalSettings.get(OLD_CFG_PREFIX + field);
            if (value == null) continue;
            globalSettings.put(NEW_CFG_PREFIX + field, value);
            globalSettings.remove(OLD_CFG_PREFIX + field);
        }
        return Collections.emptyList();
    }

    public String getPluginKey() {
        return "com.atlassian.plugins.authentication.atlassian-authentication-plugin";
    }
}

