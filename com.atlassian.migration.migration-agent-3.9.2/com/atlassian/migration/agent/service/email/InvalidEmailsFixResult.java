/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.email;

import com.atlassian.migration.agent.service.impl.MigrationUser;
import java.util.List;

public class InvalidEmailsFixResult {
    private final List<MigrationUser> invalidUsers;
    private final List<MigrationUser> tombstoneUsers;
    private final List<MigrationUser> newMailUsers;

    private InvalidEmailsFixResult(Builder builder) {
        this.invalidUsers = builder.invalidUsers;
        this.tombstoneUsers = builder.tombstoneUsers;
        this.newMailUsers = builder.newMailUsers;
    }

    public List<MigrationUser> getInvalidUsers() {
        return this.invalidUsers;
    }

    public List<MigrationUser> getTombstoneUsers() {
        return this.tombstoneUsers;
    }

    public List<MigrationUser> getNewMailUsers() {
        return this.newMailUsers;
    }

    public static class Builder {
        private List<MigrationUser> invalidUsers;
        private List<MigrationUser> tombstoneUsers;
        private List<MigrationUser> newMailUsers;

        public Builder invalidUsers(List<MigrationUser> invalidUsers) {
            this.invalidUsers = invalidUsers;
            return this;
        }

        public Builder tombstoneUsers(List<MigrationUser> tombstoneUsers) {
            this.tombstoneUsers = tombstoneUsers;
            return this;
        }

        public Builder newMailUsers(List<MigrationUser> newMailUsers) {
            this.newMailUsers = newMailUsers;
            return this;
        }

        public InvalidEmailsFixResult build() {
            return new InvalidEmailsFixResult(this);
        }
    }
}

