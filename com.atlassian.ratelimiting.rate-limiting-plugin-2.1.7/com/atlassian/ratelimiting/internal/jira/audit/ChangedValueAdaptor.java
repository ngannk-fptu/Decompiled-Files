/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.auditing.ChangedValue
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.ratelimiting.internal.jira.audit;

import com.atlassian.jira.auditing.ChangedValue;
import com.atlassian.ratelimiting.audit.AuditChangedValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChangedValueAdaptor
implements ChangedValue {
    private final AuditChangedValue changedValue;

    public ChangedValueAdaptor(AuditChangedValue changedValue) {
        this.changedValue = changedValue;
    }

    @Nonnull
    public String getName() {
        return this.changedValue.getName();
    }

    @Nullable
    public String getFrom() {
        return this.changedValue.getFrom().orElse(null);
    }

    @Nullable
    public String getTo() {
        return this.changedValue.getTo().orElse(null);
    }
}

