/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.mapper.CheckResultMapper
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.check.attachment;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckResultFileManager;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.attachment.AttachmentPathService;
import com.atlassian.migration.agent.service.check.attachment.MissingAttachmentChecker;
import com.atlassian.migration.agent.service.check.attachment.MissingAttachmentContext;
import com.atlassian.migration.agent.service.check.attachment.MissingAttachmentContextProvider;
import com.atlassian.migration.agent.service.check.attachment.MissingAttachmentMapper;
import com.atlassian.migration.agent.store.AttachmentStore;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MissingAttachmentCheckRegistration
implements CheckRegistration<MissingAttachmentContext> {
    private final MissingAttachmentChecker checker;
    private final MissingAttachmentContextProvider contextProvider;
    private final MissingAttachmentMapper mapper;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    public MissingAttachmentCheckRegistration(AnalyticsEventBuilder analyticsEventBuilder, AttachmentStore attachmentStore, AttachmentPathService attachmentPathService, SystemInformationService systemInformationService, CheckResultFileManager checkResultFileManager, AttachmentManager attachmentManager) {
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.checker = new MissingAttachmentChecker(attachmentStore, attachmentPathService, systemInformationService, checkResultFileManager, attachmentManager);
        this.contextProvider = new MissingAttachmentContextProvider();
        this.mapper = new MissingAttachmentMapper();
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.MISSING_ATTACHMENTS;
    }

    @Override
    public Checker<MissingAttachmentContext> getChecker() {
        return this.checker;
    }

    @Override
    public CheckContextProvider<MissingAttachmentContext> getCheckContextProvider() {
        return this.contextProvider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.mapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        return this.analyticsEventBuilder.buildPreflightMissingAttachments(checkResult.success, MissingAttachmentChecker.retrieveMissingAttachmentsCount(checkResult.details), totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "missingAttachmentCheck";
    }
}

