/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.util.migration;

import com.atlassian.user.util.migration.MigrationProgressListener;
import org.apache.log4j.Logger;

public class Log4jMigrationProgressListener
implements MigrationProgressListener {
    private final Logger logger;
    private int usersToMigrate;
    private int usersMigrated = 0;
    private int groupsToMigrate;
    private int groupsMigrated = 0;

    public Log4jMigrationProgressListener(Logger logger) {
        if (logger == null) {
            throw new IllegalArgumentException("Log4j logger must NOT be null");
        }
        this.logger = logger;
    }

    public void userMigrationStarted(int usersToMigrate) {
        this.usersToMigrate = usersToMigrate;
        this.logger.info((Object)("Starting user migration. " + usersToMigrate + " users to migrate."));
    }

    public void userMigrated() {
        if (++this.usersMigrated % 100 == 0) {
            this.logger.info((Object)(this.usersMigrated + " users migrated out of " + this.usersToMigrate + "."));
        }
    }

    public void userMigrationComplete() {
        this.logger.info((Object)"User migration complete.");
    }

    public void groupMigrationStarted(int groupsToMigrate) {
        this.groupsToMigrate = groupsToMigrate;
        this.logger.info((Object)("Group migration started. " + groupsToMigrate + " groups to migrate."));
    }

    public void groupMigrated() {
        if (++this.groupsMigrated % 10 == 0) {
            this.logger.info((Object)(this.groupsMigrated + " groups migrated out of " + this.groupsToMigrate + "."));
        }
    }

    public void groupMigrationComplete() {
        this.logger.info((Object)"Group migration complete.");
    }

    public void readonlyGroupMembershipNotMigrated(String groupName, String userName) {
        this.logger.warn((Object)("Could not migrate goup membership for user <" + userName + "> and group <" + groupName + ">. " + "Group is readonly, it is possibly a LDAP group."));
    }
}

