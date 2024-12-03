/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.email.EmailCheckContext
 *  com.atlassian.cmpt.check.email.EmailData
 *  com.atlassian.cmpt.check.email.EmailDuplicateChecker
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.email;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.email.EmailCheckContext;
import com.atlassian.cmpt.check.email.EmailData;
import com.atlassian.cmpt.check.email.EmailDuplicateChecker;
import com.atlassian.migration.agent.service.email.DuplicateEmailsFixResult;
import com.atlassian.migration.agent.service.email.FixAllEmailsResult;
import com.atlassian.migration.agent.service.email.GlobalUnsupportedUserHandler;
import com.atlassian.migration.agent.service.email.InvalidEmailValidator;
import com.atlassian.migration.agent.service.email.InvalidEmailsFixResult;
import com.atlassian.migration.agent.service.email.MostFrequentDomainService;
import com.atlassian.migration.agent.service.impl.MigrationUser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class UserEmailFixer {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(UserEmailFixer.class);
    private final GlobalUnsupportedUserHandler globalUnsupportedUserHandler;
    private final InvalidEmailValidator invalidEmailValidator;
    private final MostFrequentDomainService mostFrequentDomainService;

    public UserEmailFixer(GlobalUnsupportedUserHandler globalUnsupportedUserHandler, InvalidEmailValidator invalidEmailValidator, MostFrequentDomainService mostFrequentDomainService) {
        this.globalUnsupportedUserHandler = globalUnsupportedUserHandler;
        this.invalidEmailValidator = invalidEmailValidator;
        this.mostFrequentDomainService = mostFrequentDomainService;
    }

    public FixAllEmailsResult fixAllEmailsInMemory(Collection<MigrationUser> users, String cloudId) {
        this.mostFrequentDomainService.refreshMostFrequentDomainName();
        EmailCheckContext emailCheckContext = UserEmailFixer.createEmailCheckContext(users);
        Map<String, MigrationUser> userNameToUserMap = UserEmailFixer.getUserNameToUserMap(users);
        List<MigrationUser> invalidEmails = this.getUsersWithInvalidEmailsViaUMS(users, userNameToUserMap, cloudId);
        InvalidEmailsFixResult invalidEmailsFixResult = this.globalUnsupportedUserHandler.applyInvalidEmailsStrategy(invalidEmails, cloudId);
        List<MigrationUser> duplicateEmails = this.getUsersWithDuplicateEmails(emailCheckContext, userNameToUserMap);
        duplicateEmails.removeAll(invalidEmails);
        DuplicateEmailsFixResult duplicateEmailsFixResult = this.globalUnsupportedUserHandler.applyDuplicateEmailsStrategy(duplicateEmails, cloudId);
        ArrayList<MigrationUser> newMailUsers = new ArrayList<MigrationUser>();
        List<MigrationUser> invalidNewMailUsers = invalidEmailsFixResult.getNewMailUsers();
        List<MigrationUser> duplicatedNewMailUsers = duplicateEmailsFixResult.getNewMailUsers();
        if (invalidNewMailUsers != null) {
            newMailUsers.addAll(invalidNewMailUsers);
        }
        if (duplicatedNewMailUsers != null) {
            newMailUsers.addAll(duplicatedNewMailUsers);
        }
        List invalidAndDuplicateEmails = Stream.of(duplicateEmails, invalidEmails).flatMap(Collection::stream).collect(Collectors.toList());
        List<MigrationUser> validEmails = users.stream().filter(user -> !invalidAndDuplicateEmails.contains(user)).collect(Collectors.toList());
        return new FixAllEmailsResult.Builder().newMailUsers(newMailUsers).invalidUsers(invalidEmailsFixResult.getInvalidUsers()).tombstoneUsers(invalidEmailsFixResult.getTombstoneUsers()).duplicateUsers(duplicateEmailsFixResult.getDuplicateUsers()).mergeUsers(duplicateEmailsFixResult.getMergeUsers()).validUsers(validEmails).build();
    }

    @NotNull
    private static Map<String, MigrationUser> getUserNameToUserMap(Collection<MigrationUser> users) {
        return users.stream().collect(Collectors.toMap(MigrationUser::getUsername, user -> user));
    }

    @NotNull
    private static EmailCheckContext createEmailCheckContext(Collection<MigrationUser> users) {
        List emailData = users.stream().map(user -> new EmailData(user.getUsername(), user.getEmail())).collect(Collectors.toList());
        return new EmailCheckContext(emailData);
    }

    private List<MigrationUser> getUsersWithInvalidEmailsViaUMS(Collection<MigrationUser> users, Map<String, MigrationUser> userNameToUserMap, String cloudId) {
        List<EmailData> invalidEmails = this.invalidEmailValidator.getInvalidEmails(UUID.randomUUID().toString(), cloudId, users);
        return invalidEmails.stream().map(it -> (MigrationUser)userNameToUserMap.get(it.id)).collect(Collectors.toList());
    }

    private List<MigrationUser> getUsersWithDuplicateEmails(EmailCheckContext emailCheckContext, Map<String, MigrationUser> userNameToUserMap) {
        CheckResult checkResult = new EmailDuplicateChecker().check(emailCheckContext);
        List duplicateEmails = EmailDuplicateChecker.retrieveEmailDuplicates((Map)checkResult.details);
        return duplicateEmails.stream().flatMap(it -> it.ids.stream()).map(userNameToUserMap::get).collect(Collectors.toList());
    }
}

