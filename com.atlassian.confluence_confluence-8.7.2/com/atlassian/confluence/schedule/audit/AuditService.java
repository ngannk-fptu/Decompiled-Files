/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 */
package com.atlassian.confluence.schedule.audit;

import com.atlassian.confluence.schedule.audit.AuditingAction;
import com.atlassian.scheduler.config.JobId;

public interface AuditService {
    public void auditAction(JobId var1, AuditingAction var2);

    public void auditCronJobScheduleChange(JobId var1, String var2, String var3);

    public void auditSimpleJobScheduleChange(JobId var1, Long var2, Long var3);
}

