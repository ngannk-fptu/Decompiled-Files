/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.user;

import com.atlassian.migration.agent.okhttp.ErrorResponse;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GroupsConflictCheckResponse {
    @JsonProperty
    private final String taskId;
    @JsonProperty
    private final int percentageComplete;
    @JsonProperty
    private final boolean complete;
    @JsonProperty
    private final List<ErrorResponse> errors;
    @JsonProperty
    private final List<String> conflictingGroups;

    @JsonCreator
    public GroupsConflictCheckResponse(@JsonProperty(value="taskId") String taskId, @JsonProperty(value="percentageComplete") int percentageComplete, @JsonProperty(value="complete") boolean complete, @JsonProperty(value="errors") List<ErrorResponse> errors, @JsonProperty(value="conflictingGroups") List<String> conflictingGroups) {
        this.taskId = taskId;
        this.percentageComplete = percentageComplete;
        this.complete = complete;
        this.errors = errors;
        this.conflictingGroups = conflictingGroups;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public boolean isSuccessful() {
        return this.conflictingGroups.isEmpty() && this.complete && this.errors.isEmpty();
    }

    public List<String> getConflictingGroups() {
        return this.conflictingGroups;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.NO_CLASS_NAME_STYLE);
    }
}

