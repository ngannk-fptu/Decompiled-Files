/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfig
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.config.ApplicationConfig;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseValidationQueryMigrationUpgradeTask
extends AbstractUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(DatabaseValidationQueryMigrationUpgradeTask.class);
    static final String BUILD_NUMBER = "8702";
    static final String PREFERRED_VALIDATION_QUERY_KEY = "hibernate.c3p0.preferredTestQuery";
    static final String VALIDATE_KEY = "hibernate.c3p0.validate";
    static final String DATABASE_URI_KEY = "hibernate.connection.url";
    private final ApplicationConfig applicationConfig;

    public DatabaseValidationQueryMigrationUpgradeTask(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public String getBuildNumber() {
        return BUILD_NUMBER;
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public String getShortDescription() {
        return "Adds isValid() connection validation to the application config in confluence.cfg.xml (CONFSRVDEV-17301)";
    }

    public void doUpgrade() {
        try {
            if (this.applicationConfig.getProperty((Object)DATABASE_URI_KEY) != null) {
                log.info("Modifying database validation query to reflect best practice.");
                this.applicationConfig.setProperty((Object)VALIDATE_KEY, (Object)"true");
                this.applicationConfig.removeProperty((Object)PREFERRED_VALIDATION_QUERY_KEY);
                this.applicationConfig.save();
                log.info("Successfully updated database validation query.");
            } else {
                log.info("C3p0 database configuration not in use. No action required.");
            }
        }
        catch (Exception ex) {
            log.error("Unable to update database validation query.", (Throwable)ex);
        }
    }
}

