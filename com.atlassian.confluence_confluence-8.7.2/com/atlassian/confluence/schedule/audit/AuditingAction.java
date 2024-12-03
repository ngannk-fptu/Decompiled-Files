/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.schedule.audit;

import com.atlassian.confluence.impl.audit.AuditHelper;

public enum AuditingAction {
    RUN(AuditHelper.buildSummaryTextKey("scheduled.job.run")),
    ENABLE(AuditHelper.buildSummaryTextKey("scheduled.job.enabled")),
    DISABLE(AuditHelper.buildSummaryTextKey("scheduled.job.disabled"));

    private final String summaryTextKey;

    private AuditingAction(String summaryTextKey) {
        this.summaryTextKey = summaryTextKey;
    }

    public String getSummaryTextKey() {
        return this.summaryTextKey;
    }
}

