/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.audit.rest.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class AuditRetentionFileConfigJson {
    private final int maxFileCount;

    @JsonCreator
    public AuditRetentionFileConfigJson(@JsonProperty(value="maxFileCount") int maxFileCount) {
        this.maxFileCount = maxFileCount;
    }

    @JsonProperty(value="maxFileCount")
    public int getMaxFileCount() {
        return this.maxFileCount;
    }
}

