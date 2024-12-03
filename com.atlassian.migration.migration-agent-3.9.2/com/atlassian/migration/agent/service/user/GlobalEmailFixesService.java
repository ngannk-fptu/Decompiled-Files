/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.user;

import com.atlassian.migration.agent.service.email.FixAllEmailsResult;
import com.atlassian.migration.agent.service.email.UserEmailFixer;
import com.atlassian.migration.agent.service.impl.MigrationUser;
import com.atlassian.migration.agent.service.user.MigrationUsers;
import java.util.ArrayList;
import java.util.List;

public class GlobalEmailFixesService {
    private final UserEmailFixer userEmailFixer;

    public GlobalEmailFixesService(UserEmailFixer umsUserEmailFixer) {
        this.userEmailFixer = umsUserEmailFixer;
    }

    public MigrationUsers getUsersForGlobalEmailFixes(List<MigrationUser> allUsers, String cloudId) {
        FixAllEmailsResult fixAllEmailsResult = this.userEmailFixer.fixAllEmailsInMemory(allUsers, cloudId);
        ArrayList<MigrationUser> usersToMigrate = new ArrayList<MigrationUser>();
        ArrayList<MigrationUser> usersToTombstone = new ArrayList<MigrationUser>();
        if (fixAllEmailsResult.getNewMailUsers() != null) {
            usersToMigrate.addAll(fixAllEmailsResult.getNewMailUsers());
        }
        if (fixAllEmailsResult.getInvalidUsers() != null) {
            usersToMigrate.addAll(fixAllEmailsResult.getInvalidUsers());
        }
        if (fixAllEmailsResult.getDuplicateUsers() != null) {
            usersToMigrate.addAll(fixAllEmailsResult.getDuplicateUsers());
        }
        if (fixAllEmailsResult.getValidUsers() != null) {
            usersToMigrate.addAll(fixAllEmailsResult.getValidUsers());
        }
        if (fixAllEmailsResult.getMergeUsers() != null) {
            usersToMigrate.addAll(fixAllEmailsResult.getMergeUsers());
        }
        if (fixAllEmailsResult.getTombstoneUsers() != null) {
            usersToTombstone.addAll(fixAllEmailsResult.getTombstoneUsers());
        }
        return new MigrationUsers(usersToMigrate, usersToTombstone);
    }
}

