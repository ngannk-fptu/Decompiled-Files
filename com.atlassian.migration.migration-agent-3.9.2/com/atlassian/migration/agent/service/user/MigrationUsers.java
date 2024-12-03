/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.user;

import com.atlassian.migration.agent.service.impl.MigrationUser;
import java.util.Collection;
import lombok.Generated;

public class MigrationUsers {
    private final Collection<MigrationUser> usersToMigrate;
    private final Collection<MigrationUser> usersToTombstone;

    @Generated
    public MigrationUsers(Collection<MigrationUser> usersToMigrate, Collection<MigrationUser> usersToTombstone) {
        this.usersToMigrate = usersToMigrate;
        this.usersToTombstone = usersToTombstone;
    }

    @Generated
    public Collection<MigrationUser> getUsersToMigrate() {
        return this.usersToMigrate;
    }

    @Generated
    public Collection<MigrationUser> getUsersToTombstone() {
        return this.usersToTombstone;
    }
}

