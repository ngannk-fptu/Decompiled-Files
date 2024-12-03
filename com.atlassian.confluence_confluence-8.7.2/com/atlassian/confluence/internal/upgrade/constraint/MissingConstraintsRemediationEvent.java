/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.internal.upgrade.constraint;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.missing.constraints.remediation")
public class MissingConstraintsRemediationEvent {
    public static final String TRIGGER_UPGRADE = "U";
    public static final String TRIGGER_ADHOC = "A";
    private final int existingCount;
    private final int addedCount;
    private final int errorCount;
    private final String trigger;

    public MissingConstraintsRemediationEvent(int existingCount, int addedCount, int errorCount, String trigger) {
        this.existingCount = existingCount;
        this.addedCount = addedCount;
        this.errorCount = errorCount;
        this.trigger = trigger;
    }

    public int getExistingCount() {
        return this.existingCount;
    }

    public int getAddedCount() {
        return this.addedCount;
    }

    public int getErrorCount() {
        return this.errorCount;
    }

    public String getTrigger() {
        return this.trigger;
    }
}

