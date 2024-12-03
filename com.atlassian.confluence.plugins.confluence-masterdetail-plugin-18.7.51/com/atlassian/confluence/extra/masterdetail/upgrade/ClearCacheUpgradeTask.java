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
package com.atlassian.confluence.extra.masterdetail.upgrade;

import com.atlassian.confluence.extra.masterdetail.CachingDetailsManager;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={PluginUpgradeTask.class})
public class ClearCacheUpgradeTask
implements PluginUpgradeTask {
    private final CachingDetailsManager cachingDetailsManager;

    @Autowired
    public ClearCacheUpgradeTask(CachingDetailsManager cachingDetailsManager) {
        this.cachingDetailsManager = cachingDetailsManager;
    }

    public int getBuildNumber() {
        return 2;
    }

    public String getShortDescription() {
        return "Clears the page properties cache";
    }

    public Collection<Message> doUpgrade() throws Exception {
        this.cachingDetailsManager.clearCache();
        return null;
    }

    public String getPluginKey() {
        return "confluence.extra.masterdetail";
    }
}

