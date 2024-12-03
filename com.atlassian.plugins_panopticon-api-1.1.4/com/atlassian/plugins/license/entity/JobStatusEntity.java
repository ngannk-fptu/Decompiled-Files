/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugins.license.entity;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class JobStatusEntity {
    @JsonProperty(value="currentJobStartDate")
    private final String currentJobStartDate;

    @JsonCreator
    public JobStatusEntity(@JsonProperty(value="currentJobStartDate") String currentJobStartDate) {
        this.currentJobStartDate = currentJobStartDate;
    }

    public String getCurrentJobStartDate() {
        return this.currentJobStartDate;
    }
}

