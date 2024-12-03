/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.migration.agent.dto.ProgressDto;
import com.atlassian.migration.agent.dto.TaskDto;
import com.atlassian.migration.agent.entity.MigrateUsersTask;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.entity.TaskType;
import com.atlassian.migration.agent.json.JsonType;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonType(value="users-and-groups")
public class MigrateUsersTaskDto
extends TaskDto {
    @JsonProperty
    protected final boolean scoped;

    @JsonCreator
    public MigrateUsersTaskDto(@JsonProperty(value="id") String id, @JsonProperty(value="name") String name, @JsonProperty(value="migrationEstimateSeconds") long migrationEstimateSeconds, @JsonProperty(value="progress") ProgressDto progress, @JsonProperty(value="scoped") boolean scoped) {
        super(progress, id, name, migrationEstimateSeconds);
        this.scoped = scoped;
    }

    @Override
    public Task toInternalType() {
        return new MigrateUsersTask(this.scoped);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.USERS;
    }

    public boolean isScoped() {
        return this.scoped;
    }
}

