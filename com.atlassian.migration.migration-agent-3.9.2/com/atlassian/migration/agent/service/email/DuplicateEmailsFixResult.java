/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.email;

import com.atlassian.migration.agent.service.impl.MigrationUser;
import java.util.List;

public class DuplicateEmailsFixResult {
    private final List<MigrationUser> duplicateUsers;
    private final List<MigrationUser> mergeUsers;
    private final List<MigrationUser> newMailUsers;

    private DuplicateEmailsFixResult(Builder builder) {
        this.duplicateUsers = builder.duplicateUsers;
        this.mergeUsers = builder.mergeUsers;
        this.newMailUsers = builder.newMailUsers;
    }

    public List<MigrationUser> getDuplicateUsers() {
        return this.duplicateUsers;
    }

    public List<MigrationUser> getMergeUsers() {
        return this.mergeUsers;
    }

    public List<MigrationUser> getNewMailUsers() {
        return this.newMailUsers;
    }

    public static class Builder {
        private List<MigrationUser> duplicateUsers;
        private List<MigrationUser> mergeUsers;
        private List<MigrationUser> newMailUsers;

        public Builder duplicateUsers(List<MigrationUser> duplicateUsers) {
            this.duplicateUsers = duplicateUsers;
            return this;
        }

        public Builder mergeUsers(List<MigrationUser> mergeUsers) {
            this.mergeUsers = mergeUsers;
            return this;
        }

        public Builder newMailUsers(List<MigrationUser> newMailUsers) {
            this.newMailUsers = newMailUsers;
            return this;
        }

        public DuplicateEmailsFixResult build() {
            return new DuplicateEmailsFixResult(this);
        }
    }
}

