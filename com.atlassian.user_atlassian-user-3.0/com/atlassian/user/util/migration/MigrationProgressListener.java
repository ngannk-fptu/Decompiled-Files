/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.util.migration;

public interface MigrationProgressListener {
    public void userMigrationStarted(int var1);

    public void userMigrated();

    public void userMigrationComplete();

    public void groupMigrationStarted(int var1);

    public void groupMigrated();

    public void groupMigrationComplete();

    public void readonlyGroupMembershipNotMigrated(String var1, String var2);
}

