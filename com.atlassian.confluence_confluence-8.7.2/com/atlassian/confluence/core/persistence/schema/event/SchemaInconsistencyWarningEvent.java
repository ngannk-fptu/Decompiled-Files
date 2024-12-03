/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.core.persistence.schema.event;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="confluence.schemaInconsistencyWarning")
public class SchemaInconsistencyWarningEvent {
    private final int warningCount;

    public SchemaInconsistencyWarningEvent(int warningCount) {
        this.warningCount = warningCount;
    }

    public int getWarningCount() {
        return this.warningCount;
    }
}

