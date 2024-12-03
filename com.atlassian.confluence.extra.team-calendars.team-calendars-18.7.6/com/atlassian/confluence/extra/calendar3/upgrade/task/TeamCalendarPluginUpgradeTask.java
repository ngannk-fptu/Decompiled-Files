/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.extra.calendar3.util.BuildInformationManager;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import java.util.Collection;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService
public class TeamCalendarPluginUpgradeTask
implements PluginUpgradeTask {
    private final BuildInformationManager buildInformationManager;
    private final ActiveObjects activeObjects;

    @Autowired
    public TeamCalendarPluginUpgradeTask(BuildInformationManager buildInformationManager, @ComponentImport ActiveObjects activeObjects) {
        this.buildInformationManager = buildInformationManager;
        this.activeObjects = activeObjects;
    }

    public int getBuildNumber() {
        return 1312121002;
    }

    public String getShortDescription() {
        return "Trigger active object for run active objects upgrade task";
    }

    public Collection<Message> doUpgrade() throws Exception {
        return Collections.emptySet();
    }

    public String getPluginKey() {
        return this.buildInformationManager.getPluginKey();
    }
}

