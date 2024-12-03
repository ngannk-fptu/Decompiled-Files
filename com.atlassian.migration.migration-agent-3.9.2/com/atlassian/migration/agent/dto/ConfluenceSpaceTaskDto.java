/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.migration.agent.dto.ProgressDto;
import com.atlassian.migration.agent.dto.SpaceTaskDto;
import com.atlassian.migration.agent.dto.TaskDto;
import com.atlassian.migration.agent.entity.ConfluenceSpaceTask;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.entity.TaskType;
import com.atlassian.migration.agent.json.JsonType;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonType(value="confluence-space")
public class ConfluenceSpaceTaskDto
extends TaskDto
implements SpaceTaskDto {
    @JsonProperty
    private final String spaceKey;
    @JsonProperty
    private final String spaceName;

    @JsonCreator
    public ConfluenceSpaceTaskDto(@JsonProperty(value="id") String id, @JsonProperty(value="name") String name, @JsonProperty(value="spaceKey") String spaceKey, @JsonProperty(value="spaceName") String spaceName, @JsonProperty(value="migrationEstimateSeconds") long migrationEstimateSeconds, @JsonProperty(value="progress") ProgressDto progress) {
        super(progress, id, name, migrationEstimateSeconds);
        this.spaceKey = spaceKey;
        this.spaceName = spaceName;
    }

    @Override
    public String getSpace() {
        return this.spaceKey;
    }

    public String getSpaceName() {
        return this.spaceName;
    }

    @Override
    public Task toInternalType() {
        ConfluenceSpaceTask task = new ConfluenceSpaceTask();
        task.setSpaceKey(this.spaceKey);
        return task;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SPACE;
    }
}

