/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.upgradetask;

import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.confluence.plugins.synchrony.utils.SynchronyAppIdUtils;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ExportAsService
@Component
public class SynchronyAppIdUpgradeTask
implements PluginUpgradeTask {
    private final SynchronyConfigurationManager synchronyConfigurationManager;

    @Autowired
    public SynchronyAppIdUpgradeTask(SynchronyConfigurationManager synchronyConfigurationManager) {
        this.synchronyConfigurationManager = synchronyConfigurationManager;
    }

    public int getBuildNumber() {
        return 1;
    }

    public String getShortDescription() {
        return "Remove the AppID from the Bandana table if it's malformed";
    }

    public Collection<Message> doUpgrade() throws Exception {
        if (!SynchronyAppIdUtils.isValidAppId(this.synchronyConfigurationManager.getAppID())) {
            this.synchronyConfigurationManager.removeSynchronyCredentials();
        }
        return null;
    }

    public String getPluginKey() {
        return "com.atlassian.confluence.plugins.confluence-collaborative-editor-plugin";
    }
}

