/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.migration.agent.mapi.job.scope;

import org.codehaus.jackson.annotate.JsonValue;

public enum SpaceMode {
    ALL,
    ATTACHMENTS;


    @JsonValue
    public String getSpaceMode() {
        return this.toString();
    }
}

