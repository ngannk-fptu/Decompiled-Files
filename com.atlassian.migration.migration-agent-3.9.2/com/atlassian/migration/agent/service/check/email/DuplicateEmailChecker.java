/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.email.EmailCheckContext
 *  com.atlassian.cmpt.check.email.EmailData
 *  com.atlassian.cmpt.check.email.EmailDuplicateChecker
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.check.email;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.email.EmailCheckContext;
import com.atlassian.cmpt.check.email.EmailData;
import com.atlassian.cmpt.check.email.EmailDuplicateChecker;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.dto.DuplicateEmailsConfigDto;
import com.atlassian.migration.agent.dto.DuplicateEmailsStrategy;
import com.atlassian.migration.agent.service.check.email.DuplicateEmailCheckContext;
import com.atlassian.migration.agent.service.email.FixAllEmailsResult;
import com.atlassian.migration.agent.service.email.GlobalEmailFixesConfigService;
import com.atlassian.migration.agent.service.email.UserEmailFixer;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Generated;

public class DuplicateEmailChecker
implements Checker<DuplicateEmailCheckContext> {
    private final UserEmailFixer userEmailFixer;
    private final GlobalEmailFixesConfigService globalEmailFixesConfigService;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final EmailDuplicateChecker cmptEmailDuplicateChecker;

    public CheckResult check(DuplicateEmailCheckContext ctx) {
        List emailData = ctx.getMigrationUsers().stream().map(user -> new EmailData(user.getUsername(), user.getEmail())).collect(Collectors.toList());
        CheckResult checkResult = this.cmptEmailDuplicateChecker.check(new EmailCheckContext(emailData));
        if (!this.migrationDarkFeaturesManager.shouldHandleGlobalEmailFixes()) {
            return checkResult;
        }
        DuplicateEmailsConfigDto config = this.globalEmailFixesConfigService.getDuplicateEmailsConfig();
        if (!checkResult.success && config.getActionOnMigration() == DuplicateEmailsStrategy.DO_NOTHING) {
            FixAllEmailsResult fixAllEmailsResult = this.userEmailFixer.fixAllEmailsInMemory(ctx.getMigrationUsers(), ctx.getCloudId());
            return new CheckResult(fixAllEmailsResult.getDuplicateUsers().isEmpty(), checkResult.details);
        }
        return new CheckResult(true, checkResult.details);
    }

    @Generated
    public DuplicateEmailChecker(UserEmailFixer userEmailFixer, GlobalEmailFixesConfigService globalEmailFixesConfigService, MigrationDarkFeaturesManager migrationDarkFeaturesManager, EmailDuplicateChecker cmptEmailDuplicateChecker) {
        this.userEmailFixer = userEmailFixer;
        this.globalEmailFixesConfigService = globalEmailFixesConfigService;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.cmptEmailDuplicateChecker = cmptEmailDuplicateChecker;
    }
}

