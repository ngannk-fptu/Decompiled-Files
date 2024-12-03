/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.migration.agent.mapi.external.model;

import lombok.Generated;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class JobDefinitionResponse {
    private String migrationId;
    private String jobDefinition;
    private String message;

    @Generated
    public JobDefinitionResponse(String migrationId, String jobDefinition, String message) {
        this.migrationId = migrationId;
        this.jobDefinition = jobDefinition;
        this.message = message;
    }

    @Generated
    public JobDefinitionResponse() {
    }

    @Generated
    public String getMigrationId() {
        return this.migrationId;
    }

    @Generated
    public String getJobDefinition() {
        return this.jobDefinition;
    }

    @Generated
    public String getMessage() {
        return this.message;
    }
}

