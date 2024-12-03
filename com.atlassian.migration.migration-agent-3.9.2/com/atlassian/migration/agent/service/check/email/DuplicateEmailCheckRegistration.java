/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.mapper.DuplicateEmailDataProvider
 *  com.atlassian.cmpt.check.mapper.EmailDuplicateMapper
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.check.email;

import com.atlassian.cmpt.check.mapper.DuplicateEmailDataProvider;
import com.atlassian.cmpt.check.mapper.EmailDuplicateMapper;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.email.AbstractDuplicateEmailCheckRegistration;
import com.atlassian.migration.agent.service.check.email.DuplicateEmailCheckContextProvider;
import com.atlassian.migration.agent.service.email.GlobalEmailFixesConfigService;
import com.atlassian.migration.agent.service.email.UserEmailFixer;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DuplicateEmailCheckRegistration
extends AbstractDuplicateEmailCheckRegistration {
    public DuplicateEmailCheckRegistration(UserEmailFixer userEmailFixer, GlobalEmailFixesConfigService globalEmailFixesConfigService, MigrationDarkFeaturesManager migrationDarkFeaturesManager, DuplicateEmailCheckContextProvider contextProvider, SystemInformationService systemInformationService, AnalyticsEventBuilder analyticsEventBuilder) {
        super(userEmailFixer, globalEmailFixesConfigService, migrationDarkFeaturesManager, contextProvider, systemInformationService, analyticsEventBuilder);
    }

    @Override
    EmailDuplicateMapper buildResultsMapper(DuplicateEmailDataProvider dataProvider) {
        return new EmailDuplicateMapper(dataProvider);
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.SHARED_EMAILS;
    }
}

