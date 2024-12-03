/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.BootstrapException
 *  com.atlassian.confluence.upgrade.BuildNumber
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup;

import com.atlassian.config.bootstrap.BootstrapException;
import com.atlassian.confluence.upgrade.BuildNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildNumberChecker {
    private static final Logger log = LoggerFactory.getLogger(BuildNumberChecker.class);
    static final String ALLOW_DOWNGRADE = "com.atlassian.confluence.allow.downgrade";
    private final boolean clustered;

    public BuildNumberChecker(boolean clustered) {
        this.clustered = clustered;
    }

    public void checkBuildNumbers(BuildNumber homeDirectoryFinalizedBuildNumber, BuildNumber applicationBuildNumber, BuildNumber databaseFinalizedBuildNumber) throws BootstrapException {
        log.debug("Checking Confluence build number [" + applicationBuildNumber + "] against home directory with finalized build number [" + homeDirectoryFinalizedBuildNumber + "] against database with finalized build number [" + databaseFinalizedBuildNumber + "]");
        if (databaseFinalizedBuildNumber != null && !databaseFinalizedBuildNumber.equals((Object)homeDirectoryFinalizedBuildNumber)) {
            if (this.clustered) {
                if (!applicationBuildNumber.equals((Object)databaseFinalizedBuildNumber)) {
                    BuildNumberChecker.databaseBuildMismatchWithApplicationAndHomedirBuildNumbers(applicationBuildNumber, databaseFinalizedBuildNumber, homeDirectoryFinalizedBuildNumber);
                }
            } else {
                BuildNumberChecker.homedirDatabaseBuildNumberMismatch(homeDirectoryFinalizedBuildNumber, databaseFinalizedBuildNumber);
            }
        }
        if (applicationBuildNumber.isLowerThan(homeDirectoryFinalizedBuildNumber)) {
            if (BuildNumberChecker.downgradesPermitted()) {
                BuildNumberChecker.downgradeWarning(homeDirectoryFinalizedBuildNumber, applicationBuildNumber);
                return;
            }
            BuildNumberChecker.downgradeError(homeDirectoryFinalizedBuildNumber, applicationBuildNumber);
        }
    }

    private static void downgradeError(BuildNumber homeDirectoryBuildNumber, BuildNumber applicationBuildNumber) throws BootstrapException {
        throw new BootstrapException("Confluence will not start up because the finalized build number in the home directory [" + homeDirectoryBuildNumber + "] is newer than the Confluence build number [" + applicationBuildNumber + "].");
    }

    private static void downgradeWarning(BuildNumber homeDirectoryBuildNumber, BuildNumber applicationBuildNumber) {
        log.warn("Confluence (with build number " + applicationBuildNumber + " is starting up against the home directory of a newer version (build number " + homeDirectoryBuildNumber + "). Atlassian does not support running Confluence against a newer home directory.");
    }

    private static boolean downgradesPermitted() {
        return Boolean.getBoolean(ALLOW_DOWNGRADE);
    }

    private static void homedirDatabaseBuildNumberMismatch(BuildNumber homeDirectoryBuildNumber, BuildNumber databaseBuildNumber) throws BootstrapException {
        throw new BootstrapException("Confluence will not start up because the build number in the home directory [" + homeDirectoryBuildNumber + "] doesn't match the build number in the database [" + databaseBuildNumber + "].");
    }

    private static void databaseBuildMismatchWithApplicationAndHomedirBuildNumbers(BuildNumber applicationBuildNumber, BuildNumber databaseFinalizedBuildNumber, BuildNumber homeDirectoryBuildNumber) throws BootstrapException {
        throw new BootstrapException("Confluence cluster node will not start up because the finalized build number in the database [" + databaseFinalizedBuildNumber + "] doesn't match either the application build number [" + applicationBuildNumber + "] or the home directory build number [" + homeDirectoryBuildNumber + "].");
    }
}

