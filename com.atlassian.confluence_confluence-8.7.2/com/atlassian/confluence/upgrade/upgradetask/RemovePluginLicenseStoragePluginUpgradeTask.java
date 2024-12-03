/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.impl.hibernate.DataAccessUtils;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemovePluginLicenseStoragePluginUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(RemovePluginLicenseStoragePluginUpgradeTask.class);
    private final SessionFactory sessionFactory;
    private String pluginKey = "com.atlassian.upm.plugin-license-storage-plugin";

    public RemovePluginLicenseStoragePluginUpgradeTask(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void doUpgrade() throws Exception {
        log.info("Starting to uninstall the Plugin License Storage Plugin");
        DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession()).update("delete from PLUGINDATA where PLUGINKEY = ?", new Object[]{this.pluginKey});
        log.info("Finished uninstalling the Plugin License Storage Plugin.");
        log.info("Any startup errors related to the Plugin License Storage Plugin should not affect Confluence startup and should disappear after the next Confluence restart.");
    }

    public String getBuildNumber() {
        return "7202";
    }

    public String getShortDescription() {
        return "Remove the legacy Plugin License Storage plugin from the user installed plugins";
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }
}

