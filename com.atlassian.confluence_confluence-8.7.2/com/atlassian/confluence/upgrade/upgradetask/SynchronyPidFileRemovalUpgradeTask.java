/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  org.apache.commons.io.FileUtils
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import java.io.File;
import org.apache.commons.io.FileUtils;

public class SynchronyPidFileRemovalUpgradeTask
extends AbstractUpgradeTask {
    private static final String BUILD_NUMBER = "8801";
    private static final String SYNCHRONY_PID = "synchrony.pid";
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public String getBuildNumber() {
        return BUILD_NUMBER;
    }

    public void doUpgrade() throws Exception {
        File synchronyPidFile = new File(TEMP_DIR, SYNCHRONY_PID);
        if (synchronyPidFile.exists() && !FileUtils.deleteQuietly((File)synchronyPidFile)) {
            log.warn("Upgrade task failed to delete synchrony.pid file {}. This file is not used anymore and could be safely deleted manually.", (Object)synchronyPidFile.getAbsolutePath());
        }
    }
}

