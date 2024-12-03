/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckContext
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.check.email;

import com.atlassian.cmpt.check.base.CheckContext;
import com.atlassian.migration.agent.service.impl.MigrationUser;
import java.util.Collection;
import lombok.Generated;

public class DuplicateEmailCheckContext
implements CheckContext {
    private final Collection<MigrationUser> migrationUsers;
    private final String cloudId;

    @Generated
    public DuplicateEmailCheckContext(Collection<MigrationUser> migrationUsers, String cloudId) {
        this.migrationUsers = migrationUsers;
        this.cloudId = cloudId;
    }

    @Generated
    public Collection<MigrationUser> getMigrationUsers() {
        return this.migrationUsers;
    }

    @Generated
    public String getCloudId() {
        return this.cloudId;
    }
}

