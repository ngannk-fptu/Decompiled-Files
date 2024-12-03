/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.migration.agent.mapi.job.scope;

import org.codehaus.jackson.annotate.JsonValue;

public enum MapiGlobalEntitiesType {
    GLOBAL_PAGE_TEMPLATES,
    CUSTOM_SYSTEM_TEMPLATES;


    @JsonValue
    public String getGlobalTemplatesType() {
        return this.toString();
    }
}

