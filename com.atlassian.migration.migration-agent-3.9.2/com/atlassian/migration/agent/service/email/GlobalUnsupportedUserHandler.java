/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.email;

import com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.dto.DuplicateEmailsConfigDto;
import com.atlassian.migration.agent.dto.InvalidEmailsConfigDto;
import com.atlassian.migration.agent.dto.ScanSummaryDto;
import com.atlassian.migration.agent.entity.IncorrectEmail;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.email.DuplicateEmailsFixResult;
import com.atlassian.migration.agent.service.email.EmailCheckType;
import com.atlassian.migration.agent.service.email.GlobalEmailFixesConfigService;
import com.atlassian.migration.agent.service.email.InvalidEmailsFixResult;
import com.atlassian.migration.agent.service.email.NewEmailSuggestingService;
import com.atlassian.migration.agent.service.email.UserBaseScanService;
import com.atlassian.migration.agent.service.impl.MigrationUser;
import com.atlassian.migration.agent.store.IncorrectEmailStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class GlobalUnsupportedUserHandler {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(GlobalUnsupportedUserHandler.class);
    private final GlobalEmailFixesConfigService fixesConfigService;
    private final NewEmailSuggestingService newEmailSuggestingService;
    private final IncorrectEmailStore incorrectEmailStore;
    private final UserBaseScanService userBaseScanService;
    private final PluginTransactionTemplate ptx;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;

    public InvalidEmailsFixResult applyInvalidEmailsStrategy(List<MigrationUser> invalidUsers, String cloudId) {
        InvalidEmailsConfigDto config = this.fixesConfigService.getInvalidEmailsConfig();
        boolean globalEmailFixesNewEmailsFromDbEnabled = this.migrationDarkFeaturesManager.isGlobalEmailFixesNewEmailsFromDbEnabled();
        switch (config.getActionOnMigration()) {
            case DO_NOTHING: {
                return new InvalidEmailsFixResult.Builder().invalidUsers(invalidUsers).build();
            }
            case TOMBSTONE_ALL: {
                return new InvalidEmailsFixResult.Builder().tombstoneUsers(invalidUsers).build();
            }
            case USE_NEW_EMAILS: {
                if (!globalEmailFixesNewEmailsFromDbEnabled) {
                    return new InvalidEmailsFixResult.Builder().newMailUsers(this.generateNewEmails(invalidUsers, EmailCheckType.INV, cloudId)).build();
                }
                log.info("Will use MIG_INCORRECT_EMAIL table to get new emails for invalid users");
                return new InvalidEmailsFixResult.Builder().newMailUsers(this.getNewEmailsFromIncorrectEmailsTable(invalidUsers, EmailCheckType.INV, cloudId)).build();
            }
        }
        throw new IllegalStateException("Unexpected value: " + (Object)((Object)config.getActionOnMigration()));
    }

    public DuplicateEmailsFixResult applyDuplicateEmailsStrategy(List<MigrationUser> duplicateUsers, String cloudId) {
        DuplicateEmailsConfigDto config = this.fixesConfigService.getDuplicateEmailsConfig();
        boolean globalEmailFixesNewEmailsFromDbEnabled = this.migrationDarkFeaturesManager.isGlobalEmailFixesNewEmailsFromDbEnabled();
        switch (config.getActionOnMigration()) {
            case DO_NOTHING: {
                return new DuplicateEmailsFixResult.Builder().duplicateUsers(duplicateUsers).build();
            }
            case MERGE_ALL: {
                return new DuplicateEmailsFixResult.Builder().mergeUsers(duplicateUsers).build();
            }
            case USE_NEW_EMAILS: {
                if (!globalEmailFixesNewEmailsFromDbEnabled) {
                    return new DuplicateEmailsFixResult.Builder().newMailUsers(this.generateNewEmails(duplicateUsers, EmailCheckType.DUP, cloudId)).build();
                }
                log.info("Will use MIG_INCORRECT_EMAIL table to get new emails for duplicate users");
                return new DuplicateEmailsFixResult.Builder().newMailUsers(this.getNewEmailsFromIncorrectEmailsTable(duplicateUsers, EmailCheckType.DUP, cloudId)).build();
            }
        }
        throw new IllegalStateException("Unexpected value: " + (Object)((Object)config.getActionOnMigration()));
    }

    private List<IncorrectEmail> fetchIncorrectEmails(CheckType checkType) {
        ScanSummaryDto userBaseScan = this.userBaseScanService.getScanSummary();
        if (userBaseScan.getScanId() == null) {
            return Collections.emptyList();
        }
        return this.ptx.read(() -> this.incorrectEmailStore.getIncorrectEmailsByCheckType(userBaseScan.getScanId(), checkType));
    }

    private List<MigrationUser> generateNewEmails(List<MigrationUser> users, EmailCheckType type, String cloudId) {
        return users.stream().filter(user -> StringUtils.isNotBlank((CharSequence)user.getUserKey())).map(user -> new MigrationUser(user.getUserKey(), user.getUsername(), user.getFullName(), this.newEmailSuggestingService.suggest(user.getUserKey(), user.getUsername(), user.getFullName(), type, cloudId), user.isActive())).collect(Collectors.toList());
    }

    private List<MigrationUser> getNewEmailsFromIncorrectEmailsTable(List<MigrationUser> users, EmailCheckType type, String cloudId) {
        Map<String, String> userKeyToIncorrectEmail = this.fetchIncorrectEmails(type == EmailCheckType.INV ? CheckType.INVALID_EMAILS : CheckType.SHARED_EMAILS).stream().collect(Collectors.toMap(IncorrectEmail::getUserKey, e -> e.getNewEmail() != null ? IdentityAcceptedEmailValidator.cleanse((String)e.getNewEmail()) : "", (e1, e2) -> e1));
        return users.stream().filter(user -> StringUtils.isNotBlank((CharSequence)user.getUserKey())).map(user -> {
            String newEmail = (String)userKeyToIncorrectEmail.get(user.getUserKey());
            if (newEmail == null || newEmail.isEmpty()) {
                log.warn("Could not find email mapping for userKey={}, email={} will use suggested email", (Object)user.getUserKey(), (Object)user.getEmail());
                newEmail = this.newEmailSuggestingService.suggest(user.getUserKey(), user.getUsername(), user.getFullName(), type, cloudId);
            }
            return new MigrationUser(user.getUserKey(), user.getUsername(), user.getFullName(), newEmail, user.isActive());
        }).collect(Collectors.toList());
    }

    @Generated
    public GlobalUnsupportedUserHandler(GlobalEmailFixesConfigService fixesConfigService, NewEmailSuggestingService newEmailSuggestingService, IncorrectEmailStore incorrectEmailStore, UserBaseScanService userBaseScanService, PluginTransactionTemplate ptx, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        this.fixesConfigService = fixesConfigService;
        this.newEmailSuggestingService = newEmailSuggestingService;
        this.incorrectEmailStore = incorrectEmailStore;
        this.userBaseScanService = userBaseScanService;
        this.ptx = ptx;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
    }
}

