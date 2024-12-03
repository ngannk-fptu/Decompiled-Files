/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  com.atlassian.cmpt.check.dto.Status
 *  com.atlassian.cmpt.check.mapper.DuplicateEmailDataProvider
 *  com.atlassian.cmpt.check.mapper.EmailDuplicateMapper
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.check.email;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.Status;
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
public class SpacesDuplicateEmailCheckRegistration
extends AbstractDuplicateEmailCheckRegistration {
    public SpacesDuplicateEmailCheckRegistration(UserEmailFixer userEmailFixer, GlobalEmailFixesConfigService globalEmailFixesConfigService, MigrationDarkFeaturesManager migrationDarkFeaturesManager, DuplicateEmailCheckContextProvider contextProvider, SystemInformationService systemInformationService, AnalyticsEventBuilder analyticsEventBuilder) {
        super(userEmailFixer, globalEmailFixesConfigService, migrationDarkFeaturesManager, contextProvider, systemInformationService, analyticsEventBuilder);
    }

    @Override
    EmailDuplicateMapper buildResultsMapper(DuplicateEmailDataProvider dataProvider) {
        return new EmailDuplicateMapper(dataProvider){

            public void inject(CheckResultDto dto, CheckResult checkResult) {
                super.inject(dto, checkResult);
                if (dto.getStatus() == Status.ERROR) {
                    dto.setStatus(Status.WARNING);
                }
            }
        };
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.SPACES_SHARED_EMAILS;
    }
}

