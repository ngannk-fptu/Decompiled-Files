/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.email.EmailData
 *  com.atlassian.cmpt.check.email.EmailDuplicate
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.UserWithAttributes
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.email;

import com.atlassian.cmpt.check.email.EmailData;
import com.atlassian.cmpt.check.email.EmailDuplicate;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.UserWithAttributes;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.migration.agent.entity.IncorrectEmail;
import com.atlassian.migration.agent.entity.SortOrder;
import com.atlassian.migration.agent.entity.UserBaseScan;
import com.atlassian.migration.agent.entity.UserBaseScanSortKey;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.email.ActionOnMigration;
import com.atlassian.migration.agent.service.email.DuplicateEmailsFixResult;
import com.atlassian.migration.agent.service.email.EmailCheckType;
import com.atlassian.migration.agent.service.email.GlobalUnsupportedUserHandler;
import com.atlassian.migration.agent.service.email.IncorrectEmailDTO;
import com.atlassian.migration.agent.service.email.IncorrectEmailResponse;
import com.atlassian.migration.agent.service.email.InvalidEmailsFixResult;
import com.atlassian.migration.agent.service.email.NewEmailSuggestingService;
import com.atlassian.migration.agent.service.email.UserBaseScanService;
import com.atlassian.migration.agent.service.impl.MigrationUser;
import com.atlassian.migration.agent.store.IncorrectEmailStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class IncorrectEmailService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(IncorrectEmailService.class);
    static final String LAST_AUTHENTICATED_KEY = "lastAuthenticated";
    private final CrowdService crowdService;
    private final DirectoryManager directoryManager;
    private final IncorrectEmailStore incorrectEmailStore;
    private final PluginTransactionTemplate ptx;
    private final GlobalUnsupportedUserHandler globalUnsupportedUserHandler;
    private final UserBaseScanService userBaseScanService;
    private final NewEmailSuggestingService newEmailSuggestingService;

    public IncorrectEmailService(CrowdService crowdService, DirectoryManager directoryManager, IncorrectEmailStore incorrectEmailStore, PluginTransactionTemplate ptx, GlobalUnsupportedUserHandler globalUnsupportedUserHandler, UserBaseScanService userBaseScanService, NewEmailSuggestingService newEmailSuggestingService) {
        this.crowdService = crowdService;
        this.directoryManager = directoryManager;
        this.incorrectEmailStore = incorrectEmailStore;
        this.ptx = ptx;
        this.globalUnsupportedUserHandler = globalUnsupportedUserHandler;
        this.userBaseScanService = userBaseScanService;
        this.newEmailSuggestingService = newEmailSuggestingService;
    }

    public void replaceDuplicateAndInvalidEmails(String scanId, List<EmailData> invalidEmails, List<EmailDuplicate> duplicateEmails, Map<String, String> userNameToEmailMap, Map<String, String> userNameToUserkeyMap, String cloudId) {
        this.deleteAll();
        List incorrectInvalidEmails = invalidEmails.stream().map(invalidEmail -> this.toIncorrectEmail(scanId, CheckType.INVALID_EMAILS, (EmailData)invalidEmail, userNameToUserkeyMap, cloudId)).collect(Collectors.toList());
        List duplicateEmailData = duplicateEmails.stream().flatMap(duplicate -> duplicate.ids.stream().map(id -> new EmailData(id, (String)userNameToEmailMap.get(id)))).collect(Collectors.toList());
        List incorrectDuplicateEmails = duplicateEmailData.stream().map(duplicatedEmail -> this.toIncorrectEmail(scanId, CheckType.SHARED_EMAILS, (EmailData)duplicatedEmail, userNameToUserkeyMap, cloudId)).collect(Collectors.toList());
        ArrayList<IncorrectEmail> incorrectEmails = new ArrayList<IncorrectEmail>(incorrectInvalidEmails);
        incorrectEmails.addAll(incorrectDuplicateEmails);
        this.saveIncorrectEmails(incorrectEmails);
    }

    private IncorrectEmail toIncorrectEmail(String scanId, CheckType checkType, EmailData emailData, Map<String, String> userNameToUserkeyMap, String cloudId) {
        UserWithAttributes userWithAttributes = this.crowdService.getUserWithAttributes(emailData.id);
        Long directoryId = userWithAttributes.getDirectoryId();
        EmailCheckType emailCheckType = checkType == CheckType.INVALID_EMAILS ? EmailCheckType.INV : EmailCheckType.DUP;
        String userKey = userNameToUserkeyMap.get(emailData.id);
        return new IncorrectEmail(userKey, emailData.id, this.newEmailSuggestingService.suggest(userKey, userWithAttributes.getName(), userWithAttributes.getDisplayName(), emailCheckType, cloudId), emailData.email, checkType, Instant.now(), scanId, directoryId, this.getDirectoryName(directoryId), this.getLastAuthenticatedMillis(userWithAttributes).orElse(null));
    }

    private String getDirectoryName(Long directoryId) {
        try {
            return this.directoryManager.findDirectoryById(directoryId.longValue()).getName();
        }
        catch (DirectoryNotFoundException e) {
            throw new IllegalStateException(String.format("Directory %d was not found", directoryId), e);
        }
    }

    private Optional<Long> getLastAuthenticatedMillis(UserWithAttributes userWithAttributes) {
        String lastAuthenticated = userWithAttributes.getValue(LAST_AUTHENTICATED_KEY);
        if (lastAuthenticated != null) {
            return Optional.of(Long.valueOf(lastAuthenticated));
        }
        return Optional.empty();
    }

    private void saveIncorrectEmails(List<IncorrectEmail> incorrectEmails) {
        this.ptx.write(() -> incorrectEmails.forEach(this.incorrectEmailStore::save));
    }

    private void deleteAll() {
        this.ptx.write(this.incorrectEmailStore::deleteAll);
    }

    public Optional<IncorrectEmailResponse> getInvalidEmails(String scanId, String cloudId, int page, int limit, UserBaseScanSortKey sortKey, SortOrder sortOrder) {
        Optional<UserBaseScan> userBaseScan = this.userBaseScanService.get(scanId);
        if (!userBaseScan.isPresent()) {
            return Optional.empty();
        }
        long totalCount = this.incorrectEmailStore.countIncorrectEmailsByCheckType(scanId, CheckType.INVALID_EMAILS);
        List incorrectEmails = this.ptx.read(() -> this.incorrectEmailStore.getIncorrectEmailsByCheckType(scanId, CheckType.INVALID_EMAILS, page, limit, sortKey, sortOrder));
        List<MigrationUser> incorrectUsers = incorrectEmails.stream().map(IncorrectEmail::toMigrationUser).collect(Collectors.toList());
        InvalidEmailsFixResult inMemoryFix = this.globalUnsupportedUserHandler.applyInvalidEmailsStrategy(incorrectUsers, cloudId);
        List<IncorrectEmailDTO> result = this.handleInvalidEmails(incorrectEmails, inMemoryFix);
        if (sortKey == UserBaseScanSortKey.ON_MIGRATION) {
            IncorrectEmailService.sortResultByOnMigration(result, sortOrder);
        }
        return Optional.of(new IncorrectEmailResponse(scanId, page, limit, totalCount, result));
    }

    private List<IncorrectEmailDTO> handleInvalidEmails(List<IncorrectEmail> emailData, InvalidEmailsFixResult fixedInvalidEmails) {
        Map<String, ActionOnMigration> invalidMap = Collections.emptyMap();
        Map<String, ActionOnMigration> tombstoneMap = Collections.emptyMap();
        Map<String, List<MigrationUser>> newEmails = Collections.emptyMap();
        if (fixedInvalidEmails.getNewMailUsers() != null) {
            newEmails = fixedInvalidEmails.getNewMailUsers().stream().collect(Collectors.groupingBy(MigrationUser::getUsername));
        }
        if (fixedInvalidEmails.getInvalidUsers() != null) {
            invalidMap = fixedInvalidEmails.getInvalidUsers().stream().collect(Collectors.toMap(MigrationUser::getUsername, value -> ActionOnMigration.DO_NOTHING));
        }
        if (fixedInvalidEmails.getTombstoneUsers() != null) {
            tombstoneMap = fixedInvalidEmails.getTombstoneUsers().stream().collect(Collectors.toMap(MigrationUser::getUsername, value -> ActionOnMigration.TOMBSTONE));
        }
        return this.getIncorrectEmailData(emailData, invalidMap, tombstoneMap, newEmails);
    }

    public Optional<IncorrectEmailResponse> getDuplicateEmails(String scanId, String cloudId, int page, int limit, UserBaseScanSortKey sortKey, SortOrder sortOrder) {
        Optional<UserBaseScan> userBaseScan = this.userBaseScanService.get(scanId);
        if (!userBaseScan.isPresent()) {
            return Optional.empty();
        }
        long totalCount = this.incorrectEmailStore.countIncorrectEmailsByCheckType(scanId, CheckType.SHARED_EMAILS);
        List incorrectEmails = this.ptx.read(() -> this.incorrectEmailStore.getIncorrectEmailsByCheckType(scanId, CheckType.SHARED_EMAILS, page, limit, sortKey, sortOrder));
        List<MigrationUser> duplicateUsers = incorrectEmails.stream().map(IncorrectEmail::toMigrationUser).collect(Collectors.toList());
        DuplicateEmailsFixResult inMemoryFix = this.globalUnsupportedUserHandler.applyDuplicateEmailsStrategy(duplicateUsers, cloudId);
        List<IncorrectEmailDTO> result = this.handleDuplicateEmails(incorrectEmails, inMemoryFix);
        if (sortKey == UserBaseScanSortKey.ON_MIGRATION) {
            IncorrectEmailService.sortResultByOnMigration(result, sortOrder);
        }
        return Optional.of(new IncorrectEmailResponse(scanId, page, limit, totalCount, result));
    }

    private static void sortResultByOnMigration(List<IncorrectEmailDTO> result, SortOrder sortOrder) {
        Comparator<IncorrectEmailDTO> comparator = Comparator.comparing(IncorrectEmailDTO::getActionOnMigration);
        if (sortOrder == SortOrder.DESC) {
            comparator.reversed();
        }
        result.sort(comparator);
    }

    private List<IncorrectEmailDTO> handleDuplicateEmails(List<IncorrectEmail> emailData, DuplicateEmailsFixResult duplicateEmailsFixResult) {
        Map<String, ActionOnMigration> duplicateMap = Collections.emptyMap();
        Map<String, ActionOnMigration> mergeMap = Collections.emptyMap();
        Map<String, List<MigrationUser>> newEmails = Collections.emptyMap();
        if (duplicateEmailsFixResult.getNewMailUsers() != null) {
            newEmails = duplicateEmailsFixResult.getNewMailUsers().stream().collect(Collectors.groupingBy(MigrationUser::getUsername));
        }
        if (duplicateEmailsFixResult.getDuplicateUsers() != null) {
            duplicateMap = duplicateEmailsFixResult.getDuplicateUsers().stream().collect(Collectors.toMap(MigrationUser::getUsername, value -> ActionOnMigration.DO_NOTHING));
        }
        if (duplicateEmailsFixResult.getMergeUsers() != null) {
            mergeMap = duplicateEmailsFixResult.getMergeUsers().stream().collect(Collectors.toMap(MigrationUser::getUsername, value -> ActionOnMigration.MERGE));
        }
        return this.getIncorrectEmailData(emailData, duplicateMap, mergeMap, newEmails);
    }

    @NotNull
    private List<IncorrectEmailDTO> getIncorrectEmailData(List<IncorrectEmail> emailData, Map<String, ActionOnMigration> unfixedUsersMap, Map<String, ActionOnMigration> fixedUsersMap, Map<String, List<MigrationUser>> newEmails) {
        Map<String, ActionOnMigration> concatMap = Stream.of(unfixedUsersMap, fixedUsersMap).flatMap(map -> map.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return emailData.stream().map(incorrectEmail -> {
            ActionOnMigration defaultActionOnMigration = ActionOnMigration.DO_NOTHING;
            if (newEmails.containsKey(incorrectEmail.getUserName())) {
                incorrectEmail.setNewEmail(((MigrationUser)((List)newEmails.get(incorrectEmail.getUserName())).get(0)).getEmail());
                defaultActionOnMigration = ActionOnMigration.USE_NEW_EMAIL;
            }
            return IncorrectEmailDTO.fromIncorrectEmail(incorrectEmail, concatMap.getOrDefault(incorrectEmail.getUserName(), defaultActionOnMigration));
        }).collect(Collectors.toList());
    }
}

