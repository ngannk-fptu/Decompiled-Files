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

public class JobIdentifierEntity {
    @JsonProperty(value="jobIdentifier")
    private final String jobIdentifier;

    @JsonCreator
    public JobIdentifierEntity(@JsonProperty(value="jobIdentifier") String jobIdentifier) {
        this.jobIdentifier = jobIdentifier;
    }

    public String getJobIdentifier() {
        return this.jobIdentifier;
    }
}

