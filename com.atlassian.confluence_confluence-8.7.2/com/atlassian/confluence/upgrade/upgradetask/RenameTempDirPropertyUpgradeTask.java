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

public class RenameTempDirPropertyUpgradeTask
extends AbstractUpgradeTask {
    public static final String LEGACY_TEMP_DIR_PROP = "webwork.multipart.saveDir";
    public static final String NEXT_TEMP_DIR_PROP = "struts.multipart.saveDir";
    private static final Logger log = LoggerFactory.getLogger(RenameTempDirPropertyUpgradeTask.class);
    private static final String BUILD_NUMBER = "9002";
    private final ApplicationConfig applicationConfig;

    public RenameTempDirPropertyUpgradeTask(ApplicationConfig applicationConfig) {
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
        return String.format("Checks application configuration (usually confluence.cfg.xml file) and renames old WebWork property '%s' to the new Struts property: '%s'. The value of the property left intact.", LEGACY_TEMP_DIR_PROP, NEXT_TEMP_DIR_PROP);
    }

    public void doUpgrade() throws Exception {
        Object multipartDir = this.applicationConfig.getProperty((Object)LEGACY_TEMP_DIR_PROP);
        if (multipartDir == null) {
            log.info("'{}' property is not defined; nothing to upgrade.", (Object)LEGACY_TEMP_DIR_PROP);
            return;
        }
        log.info("Found definition {}='{}'; renaming {} -> {}", new Object[]{LEGACY_TEMP_DIR_PROP, multipartDir, LEGACY_TEMP_DIR_PROP, NEXT_TEMP_DIR_PROP});
        this.applicationConfig.setProperty((Object)NEXT_TEMP_DIR_PROP, multipartDir);
        this.applicationConfig.removeProperty((Object)LEGACY_TEMP_DIR_PROP);
        this.applicationConfig.save();
        log.info("Successfully saved application config.");
    }
}

