/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.scheduler.config.JobId
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.schedule.audit;

import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.impl.audit.AuditCategories;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.schedule.audit.AuditService;
import com.atlassian.confluence.schedule.audit.AuditingAction;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.scheduler.config.JobId;
import java.util.Arrays;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAuditService
implements AuditService {
    private static final Logger auditLog = LoggerFactory.getLogger(AuditService.class);
    private static final String NO_USERNAME = "*SYSTEM*";
    private static final String SCHEDULED_JOBS_EDITED_SUMMARY = AuditHelper.buildSummaryTextKey("scheduled.job.edited");
    public static final String SCHEDULED_JOB_SCHEDULE = AuditHelper.buildChangedValueTextKey("scheduled.job.schedule");
    public static final String SCHEDULED_JOB_INTERVAL = AuditHelper.buildChangedValueTextKey("scheduled.job.interval");
    private final com.atlassian.audit.api.AuditService auditService;
    private final AuditHelper auditHelper;
    private final StandardAuditResourceTypes resourceTypes;

    public DefaultAuditService(com.atlassian.audit.api.AuditService auditService, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes) {
        this.auditService = auditService;
        this.auditHelper = auditHelper;
        this.resourceTypes = resourceTypes;
    }

    @Override
    public void auditAction(JobId jobId, AuditingAction action) {
        auditLog.info("User: {} performed action: {} on job {}", new Object[]{this.getUserName(), action, jobId});
        this.audit(jobId, this.getAuditType(action), new ChangedValue[0]);
    }

    @Override
    public void auditCronJobScheduleChange(JobId jobId, String oldValue, String newValue) {
        auditLog.info("User: {} changed cron schedule: from '{}' to '{}' on job {}", new Object[]{this.getUserName(), oldValue, newValue, jobId});
        this.audit(jobId, this.getAuditType(SCHEDULED_JOBS_EDITED_SUMMARY), () -> ChangedValue.fromI18nKeys((String)SCHEDULED_JOB_SCHEDULE).from(oldValue).to(newValue).build());
    }

    @Override
    public void auditSimpleJobScheduleChange(JobId jobId, Long oldValue, Long newValue) {
        auditLog.info("User: {} changed simple schedule: from '{}' to '{}' on job {}", new Object[]{this.getUserName(), oldValue, newValue, jobId});
        this.audit(jobId, this.getAuditType(SCHEDULED_JOBS_EDITED_SUMMARY), () -> ChangedValue.fromI18nKeys((String)SCHEDULED_JOB_INTERVAL).from(String.valueOf(oldValue)).to(String.valueOf(newValue)).build());
    }

    private void audit(JobId jobId, AuditType auditType, Supplier<ChangedValue> changedValueSupplier) {
        this.audit(jobId, auditType, changedValueSupplier.get());
    }

    private void audit(JobId jobId, AuditType auditType, ChangedValue ... changedValues) {
        try {
            AuditResource affectedObject = AuditResource.builder((String)jobId.toString(), (String)this.resourceTypes.scheduledJob()).id(jobId.toString()).build();
            AuditEvent.Builder auditEventBuilder = AuditEvent.builder((AuditType)auditType).affectedObject(affectedObject);
            if (changedValues != null && changedValues.length > 0) {
                auditEventBuilder.changedValues(Arrays.asList(changedValues));
            }
            this.auditService.audit(auditEventBuilder.build());
        }
        catch (Exception e) {
            auditLog.warn("Cannot process auditing event of type {}", (Object)auditType);
            auditLog.debug("Error processing auditing event of type {}", (Object)auditType, (Object)e);
        }
    }

    private AuditType getAuditType(AuditingAction action) {
        return this.getAuditType(action.getSummaryTextKey());
    }

    private AuditType getAuditType(String summaryKey) {
        return AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.ADVANCED, (String)AuditCategories.ADMIN, (String)summaryKey).build();
    }

    private String getUserName() {
        String username = AuthenticatedUserThreadLocal.getUsername();
        return username == null ? NO_USERNAME : username;
    }
}

