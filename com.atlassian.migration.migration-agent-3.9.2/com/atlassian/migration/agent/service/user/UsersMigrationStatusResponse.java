/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.user;

import com.atlassian.migration.agent.okhttp.ErrorResponse;
import com.atlassian.migration.agent.service.user.UserMigrationStatus;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Generated;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class UsersMigrationStatusResponse {
    @JsonProperty
    private final String taskId;
    @JsonProperty
    private final int progressPercentage;
    @JsonProperty
    private final UserMigrationStatus status;
    @JsonProperty
    private final List<ErrorResponse> errors;
    @JsonProperty
    private final int totalUsersCount;
    @JsonProperty
    private final int migratedUsersCount;
    @JsonProperty
    private final int totalGroupsCount;
    @JsonProperty
    private final int migratedGroupsCount;

    @JsonCreator
    public UsersMigrationStatusResponse(@JsonProperty(value="taskId") String taskId, @JsonProperty(value="progressPercentage") int progressPercentage, @JsonProperty(value="status") UserMigrationStatus status, @JsonProperty(value="errors") List<ErrorResponse> errors, @JsonProperty(value="totalUsersCount") int totalUsersCount, @JsonProperty(value="migratedUsersCount") int migratedUsersCount, @JsonProperty(value="totalGroupsCount") int totalGroupsCount, @JsonProperty(value="migratedGroupsCount") int migratedGroupsCount) {
        this.taskId = Objects.requireNonNull(taskId);
        this.progressPercentage = progressPercentage;
        this.status = status;
        this.errors = errors;
        this.totalUsersCount = totalUsersCount;
        this.migratedUsersCount = migratedUsersCount;
        this.totalGroupsCount = totalGroupsCount;
        this.migratedGroupsCount = migratedGroupsCount;
    }

    public boolean isSuccessful() {
        return this.status == UserMigrationStatus.DONE;
    }

    public boolean isComplete() {
        return this.status != UserMigrationStatus.IN_PROGRESS;
    }

    public Optional<String> getFirstErrorMessage() {
        return this.errors.stream().map(error -> error.message).findFirst();
    }

    public List<Integer> getErrorCodes() {
        if (this.errors == null) {
            return Collections.emptyList();
        }
        return this.errors.stream().map(error -> error.code).collect(Collectors.toList());
    }

    public List<String> getErrorMessages() {
        if (this.errors == null) {
            return Collections.emptyList();
        }
        return this.errors.stream().map(error -> error.message).collect(Collectors.toList());
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    @Generated
    public String getTaskId() {
        return this.taskId;
    }

    @Generated
    public int getProgressPercentage() {
        return this.progressPercentage;
    }

    @Generated
    public UserMigrationStatus getStatus() {
        return this.status;
    }

    @Generated
    public List<ErrorResponse> getErrors() {
        return this.errors;
    }

    @Generated
    public int getTotalUsersCount() {
        return this.totalUsersCount;
    }

    @Generated
    public int getMigratedUsersCount() {
        return this.migratedUsersCount;
    }

    @Generated
    public int getTotalGroupsCount() {
        return this.totalGroupsCount;
    }

    @Generated
    public int getMigratedGroupsCount() {
        return this.migratedGroupsCount;
    }
}

