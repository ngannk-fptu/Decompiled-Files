/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 */
package com.atlassian.user.util.migration;

import com.atlassian.user.util.migration.MigrationProgressListener;
import org.slf4j.Logger;

public class Slf4jMigrationProgressListener
implements MigrationProgressListener {
    private final Logger logger;
    private int usersToMigrate;
    private int usersMigrated = 0;
    private int groupsToMigrate;
    private int groupsMigrated = 0;

    public Slf4jMigrationProgressListener(Logger logger) {
        if (logger == null) {
            throw new IllegalArgumentException("Logger must NOT be null");
        }
        this.logger = logger;
    }

    public void userMigrationStarted(int usersToMigrate) {
        this.usersToMigrate = usersToMigrate;
        this.logger.info("Starting user migration. {} users to migrate.", (Object)usersToMigrate);
    }

    public void userMigrated() {
        if (++this.usersMigrated % 100 == 0) {
            this.logger.info("{} users migrated out of {}.", (Object)this.usersMigrated, (Object)this.usersToMigrate);
        }
    }

    public void userMigrationComplete() {
        this.logger.info("User migration complete.");
    }

    public void groupMigrationStarted(int groupsToMigrate) {
        this.groupsToMigrate = groupsToMigrate;
        this.logger.info("Group migration started. {} groups to migrate.", (Object)groupsToMigrate);
    }

    public void groupMigrated() {
        if (++this.groupsMigrated % 10 == 0) {
            this.logger.info("{} groups migrated out of {}.", (Object)this.groupsMigrated, (Object)this.groupsToMigrate);
        }
    }

    public void groupMigrationComplete() {
        this.logger.info("Group migration complete.");
    }

    public void readonlyGroupMembershipNotMigrated(String groupName, String userName) {
        this.logger.warn("Could not migrate group membership for user <{}> and group <{}>. Group is read-only; it is possibly a LDAP group.", (Object)userName, (Object)groupName);
    }
}

