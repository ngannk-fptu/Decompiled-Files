/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.roadmap.upgradetask;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugins.roadmap.upgradetask.CurrentSpacesParamMigrator;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrentSpacesParamUpgradeTask
implements PluginUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(CurrentSpacesParamUpgradeTask.class);
    private final CurrentSpacesParamMigrator migrator;

    public CurrentSpacesParamUpgradeTask(CurrentSpacesParamMigrator migrator) {
        this.migrator = migrator;
    }

    public int getBuildNumber() {
        return 6;
    }

    public String getShortDescription() {
        return "Remove the \"currentspaces\" parameter from Roadmap macros";
    }

    public Collection<Message> doUpgrade() throws Exception {
        log.info("Starting to migrate Roadmap macros");
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        this.migrator.migrate();
        AuthenticatedUserThreadLocal.set((ConfluenceUser)currentUser);
        return null;
    }

    public String getPluginKey() {
        return "com.atlassian.confluence.plugins.confluence-roadmap-plugin";
    }
}

