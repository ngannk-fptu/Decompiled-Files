/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceStatus
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.plugins.ia.upgrade;

import com.atlassian.confluence.plugins.ia.service.SidebarService;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class NavTypeDefaultUpgradeTask
implements PluginUpgradeTask {
    private final SidebarService sidebarService;
    private final SpaceManager spaceManager;

    public NavTypeDefaultUpgradeTask(SidebarService sidebarService, SpaceManager spaceManager) {
        this.sidebarService = sidebarService;
        this.spaceManager = spaceManager;
    }

    public int getBuildNumber() {
        return 1;
    }

    public String getShortDescription() {
        return "Ensuring original default of children nav type is preserved.";
    }

    public Collection<Message> doUpgrade() throws Exception {
        ArrayList spaceKeys = Lists.newArrayList();
        spaceKeys.addAll(this.spaceManager.getAllSpaceKeys(SpaceStatus.CURRENT));
        spaceKeys.addAll(this.spaceManager.getAllSpaceKeys(SpaceStatus.ARCHIVED));
        for (String spaceKey : spaceKeys) {
            if (this.sidebarService.getOption(spaceKey, "nav-type") != null) continue;
            this.sidebarService.forceSetOption(spaceKey, "nav-type", "pages");
        }
        return Collections.EMPTY_LIST;
    }

    public String getPluginKey() {
        return "com.atlassian.confluence.plugins.confluence-space-ia";
    }
}

