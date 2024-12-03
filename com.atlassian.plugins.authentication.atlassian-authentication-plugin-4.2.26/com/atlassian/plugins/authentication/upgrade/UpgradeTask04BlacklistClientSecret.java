/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  com.google.common.annotations.VisibleForTesting
 *  javax.inject.Inject
 *  org.springframework.stereotype.Component
 */
package com.atlassian.plugins.authentication.upgrade;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.upgrade.UpgradeUtils;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collection;
import java.util.Collections;
import javax.inject.Inject;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={PluginUpgradeTask.class})
public class UpgradeTask04BlacklistClientSecret
implements PluginUpgradeTask {
    @VisibleForTesting
    static final String CFG_PREFIX = "com.atlassian.plugins.authentication.sso.config.";
    @VisibleForTesting
    static final String SUPPORT_ZIP_BLACKLISTED_PREFIX = "License.com.atlassian.plugins.authentication.sso.config.";
    @VisibleForTesting
    static final String CLIENT_SECRET = "client-secret";
    private final PluginSettingsFactory pluginSettings;

    @Inject
    public UpgradeTask04BlacklistClientSecret(@ComponentImport PluginSettingsFactory pluginSettings) {
        this.pluginSettings = pluginSettings;
    }

    public int getBuildNumber() {
        return 4;
    }

    public String getShortDescription() {
        return "Prefix client secret with blacklisted prefix.";
    }

    public Collection<Message> doUpgrade() throws Exception {
        UpgradeUtils.rename(this.pluginSettings.createGlobalSettings(), "com.atlassian.plugins.authentication.sso.config.client-secret", "License.com.atlassian.plugins.authentication.sso.config.client-secret");
        return Collections.emptyList();
    }

    public String getPluginKey() {
        return "com.atlassian.plugins.authentication.atlassian-authentication-plugin";
    }
}

