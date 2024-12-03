/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.mapi.job;

import com.atlassian.migration.agent.mapi.external.model.JobDetails;
import com.atlassian.migration.agent.mapi.job.scope.CloudDetails;
import com.atlassian.migration.agent.mapi.job.scope.Scope;
import com.atlassian.migration.agent.mapi.job.scope.ServerDetails;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class JobDefinition
implements JobDetails {
    @JsonProperty
    private String migrationId;
    @JsonProperty
    private String flow;
    @JsonProperty
    private String name;
    @JsonProperty
    private ServerDetails source;
    @JsonProperty
    private CloudDetails destination;
    @JsonProperty
    private Scope scope;

    @Generated
    public JobDefinition(String migrationId, String flow, String name, ServerDetails source, CloudDetails destination, Scope scope) {
        this.migrationId = migrationId;
        this.flow = flow;
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.scope = scope;
    }

    @Generated
    public JobDefinition() {
    }

    @Generated
    public String getMigrationId() {
        return this.migrationId;
    }

    @Generated
    public String getFlow() {
        return this.flow;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public ServerDetails getSource() {
        return this.source;
    }

    @Generated
    public CloudDetails getDestination() {
        return this.destination;
    }

    @Generated
    public Scope getScope() {
        return this.scope;
    }

    @Generated
    public void setMigrationId(String migrationId) {
        this.migrationId = migrationId;
    }
}

