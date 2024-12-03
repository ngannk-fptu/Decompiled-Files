/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.util.migration;

public class MigratorConfiguration {
    private boolean migrateMembershipsForExistingUsers;

    public boolean isMigrateMembershipsForExistingUsers() {
        return this.migrateMembershipsForExistingUsers;
    }

    public void setMigrateMembershipsForExistingUsers(boolean migrateMembershipsForExistingUsers) {
        this.migrateMembershipsForExistingUsers = migrateMembershipsForExistingUsers;
    }
}

