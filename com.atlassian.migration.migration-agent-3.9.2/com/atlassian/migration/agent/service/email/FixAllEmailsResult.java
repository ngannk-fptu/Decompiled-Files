/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.email;

import com.atlassian.migration.agent.service.impl.MigrationUser;
import java.util.List;

public class FixAllEmailsResult {
    private final List<MigrationUser> newMailUsers;
    private final List<MigrationUser> invalidUsers;
    private final List<MigrationUser> tombstoneUsers;
    private final List<MigrationUser> duplicateUsers;
    private final List<MigrationUser> mergeUsers;
    private final List<MigrationUser> validUsers;

    public FixAllEmailsResult(Builder builder) {
        this.newMailUsers = builder.newMailUsers;
        this.invalidUsers = builder.invalidUsers;
        this.tombstoneUsers = builder.tombstoneUsers;
        this.duplicateUsers = builder.duplicateUsers;
        this.mergeUsers = builder.mergeUsers;
        this.validUsers = builder.validUsers;
    }

    public List<MigrationUser> getNewMailUsers() {
        return this.newMailUsers;
    }

    public List<MigrationUser> getInvalidUsers() {
        return this.invalidUsers;
    }

    public List<MigrationUser> getTombstoneUsers() {
        return this.tombstoneUsers;
    }

    public List<MigrationUser> getDuplicateUsers() {
        return this.duplicateUsers;
    }

    public List<MigrationUser> getMergeUsers() {
        return this.mergeUsers;
    }

    public List<MigrationUser> getValidUsers() {
        return this.validUsers;
    }

    public static class Builder {
        private List<MigrationUser> newMailUsers;
        private List<MigrationUser> invalidUsers;
        private List<MigrationUser> tombstoneUsers;
        private List<MigrationUser> duplicateUsers;
        private List<MigrationUser> mergeUsers;
        private List<MigrationUser> validUsers;

        public Builder newMailUsers(List<MigrationUser> newMailUsers) {
            this.newMailUsers = newMailUsers;
            return this;
        }

        public Builder invalidUsers(List<MigrationUser> invalidUsers) {
            this.invalidUsers = invalidUsers;
            return this;
        }

        public Builder tombstoneUsers(List<MigrationUser> tombstoneUsers) {
            this.tombstoneUsers = tombstoneUsers;
            return this;
        }

        public Builder duplicateUsers(List<MigrationUser> duplicateUsers) {
            this.duplicateUsers = duplicateUsers;
            return this;
        }

        public Builder mergeUsers(List<MigrationUser> mergeUsers) {
            this.mergeUsers = mergeUsers;
            return this;
        }

        public Builder validUsers(List<MigrationUser> validUsers) {
            this.validUsers = validUsers;
            return this;
        }

        public FixAllEmailsResult build() {
            return new FixAllEmailsResult(this);
        }
    }
}

