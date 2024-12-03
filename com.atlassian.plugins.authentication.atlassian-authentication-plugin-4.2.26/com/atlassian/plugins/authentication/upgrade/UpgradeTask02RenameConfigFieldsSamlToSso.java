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
 *  org.springframework.stereotype.Component
 */
package com.atlassian.plugins.authentication.upgrade;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.upgrade.UpgradeUtils;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.inject.Inject;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={PluginUpgradeTask.class})
public class UpgradeTask02RenameConfigFieldsSamlToSso
implements PluginUpgradeTask {
    @VisibleForTesting
    static final String CFG_PREFIX = "com.atlassian.plugins.authentication.sso.config.";
    @VisibleForTesting
    static final Map<String, String> FIELDS_TO_RENAME = ImmutableMap.of((Object)"allow-saml-override", (Object)"allow-redirect-override");
    private final PluginSettingsFactory pluginSettings;

    @Inject
    public UpgradeTask02RenameConfigFieldsSamlToSso(@ComponentImport PluginSettingsFactory pluginSettings) {
        this.pluginSettings = pluginSettings;
    }

    public int getBuildNumber() {
        return 2;
    }

    public String getShortDescription() {
        return "Rename generic SSO config fields which contain 'SAML' keyword, to allow future non-SAML protocols";
    }

    public Collection<Message> doUpgrade() throws Exception {
        PluginSettings globalSettings = this.pluginSettings.createGlobalSettings();
        for (Map.Entry<String, String> entry : FIELDS_TO_RENAME.entrySet()) {
            UpgradeUtils.rename(globalSettings, CFG_PREFIX + entry.getKey(), CFG_PREFIX + entry.getValue());
        }
        return Collections.emptyList();
    }

    public String getPluginKey() {
        return "com.atlassian.plugins.authentication.atlassian-authentication-plugin";
    }
}

