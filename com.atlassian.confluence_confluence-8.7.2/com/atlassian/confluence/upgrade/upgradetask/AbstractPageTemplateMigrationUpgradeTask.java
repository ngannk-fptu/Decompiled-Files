/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractDeferredRunUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.content.render.xhtml.migration.SiteMigrator;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.ExceptionReport;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.ExceptionReportUtil;
import com.atlassian.confluence.upgrade.AbstractDeferredRunUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;

public abstract class AbstractPageTemplateMigrationUpgradeTask
extends AbstractDeferredRunUpgradeTask
implements DatabaseUpgradeTask {
    private final SiteMigrator migrator;

    public AbstractPageTemplateMigrationUpgradeTask(SiteMigrator migrator) {
        this.migrator = migrator;
    }

    public void doDeferredUpgrade() throws Exception {
        ExceptionReport report = this.migrator.migrateSite();
        if (report.isErrored()) {
            String reportString = ExceptionReportUtil.generateReportString("Page Template XHTML Round Trip Exception Report", report, log.isDebugEnabled());
            log.warn(reportString);
        }
    }

    public boolean runOnSpaceImport() {
        return true;
    }

    public boolean breaksBackwardCompatibility() {
        return true;
    }
}

