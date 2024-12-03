/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.email.EmailDuplicateChecker
 *  com.atlassian.cmpt.check.mapper.CheckResultMapper
 *  com.atlassian.cmpt.check.mapper.DuplicateEmailDataProvider
 *  com.atlassian.cmpt.check.mapper.EmailDuplicateMapper
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.migration.agent.service.check.email;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.email.EmailDuplicateChecker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.cmpt.check.mapper.DuplicateEmailDataProvider;
import com.atlassian.cmpt.check.mapper.EmailDuplicateMapper;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.email.DuplicateEmailCheckContext;
import com.atlassian.migration.agent.service.check.email.DuplicateEmailCheckContextProvider;
import com.atlassian.migration.agent.service.check.email.DuplicateEmailChecker;
import com.atlassian.migration.agent.service.email.GlobalEmailFixesConfigService;
import com.atlassian.migration.agent.service.email.UserEmailFixer;
import com.atlassian.migration.agent.service.version.ConfluenceServerVersion;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.web.util.UriComponentsBuilder;

@ParametersAreNonnullByDefault
abstract class AbstractDuplicateEmailCheckRegistration
implements CheckRegistration<DuplicateEmailCheckContext> {
    private static final String PATH = "/admin/users/dosearchusers.action";
    private final DuplicateEmailChecker checker;
    private final DuplicateEmailCheckContextProvider contextProvider;
    private final EmailDuplicateMapper resultMapper;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    AbstractDuplicateEmailCheckRegistration(UserEmailFixer userEmailFixer, GlobalEmailFixesConfigService globalEmailFixesConfigService, MigrationDarkFeaturesManager migrationDarkFeaturesManager, DuplicateEmailCheckContextProvider contextProvider, SystemInformationService systemInformationService, AnalyticsEventBuilder analyticsEventBuilder) {
        this.contextProvider = contextProvider;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.checker = new DuplicateEmailChecker(userEmailFixer, globalEmailFixesConfigService, migrationDarkFeaturesManager, new EmailDuplicateChecker());
        ConfluenceInfo confluenceInfo = systemInformationService.getConfluenceInfo();
        ConfluenceServerVersion version = ConfluenceServerVersion.of(confluenceInfo.getVersion());
        String searchParam = version.greaterOrEqual("6.14.0") && version.lessThan("8.0.0") ? "emailTerm" : "searchTerm";
        this.resultMapper = this.buildResultsMapper(email -> UriComponentsBuilder.fromHttpUrl((String)confluenceInfo.getBaseUrl()).path(PATH).replaceQueryParam(searchParam, new Object[]{email}).toUriString());
    }

    abstract EmailDuplicateMapper buildResultsMapper(DuplicateEmailDataProvider var1);

    public DuplicateEmailChecker getChecker() {
        return this.checker;
    }

    public DuplicateEmailCheckContextProvider getCheckContextProvider() {
        return this.contextProvider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.resultMapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        return this.analyticsEventBuilder.buildPreflightDuplicateEmails(checkResult.success, EmailDuplicateChecker.retrieveEmailDuplicates((Map)checkResult.details), totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "sharedEmailsCheck";
    }
}

