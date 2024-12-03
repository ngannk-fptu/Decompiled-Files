/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.WordUtils
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonTypeInfo
 *  org.codehaus.jackson.annotate.JsonTypeInfo$As
 *  org.codehaus.jackson.annotate.JsonTypeInfo$Id
 *  org.codehaus.jackson.map.annotate.JsonTypeIdResolver
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.migration.agent.dto.ProgressDto;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.entity.TaskType;
import com.atlassian.migration.agent.json.JsonParentType;
import com.atlassian.migration.agent.json.JsonParentTypeIdResolver;
import java.util.Objects;
import org.apache.commons.lang.WordUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonTypeIdResolver;

@JsonTypeInfo(use=JsonTypeInfo.Id.CUSTOM, include=JsonTypeInfo.As.PROPERTY, property="@type")
@JsonTypeIdResolver(value=JsonParentTypeIdResolver.class)
@JsonParentType
public abstract class TaskDto {
    @JsonProperty
    protected final ProgressDto progress;
    @JsonProperty
    protected final String id;
    @JsonProperty
    protected final String name;
    @JsonProperty
    protected long migrationEstimateSeconds;
    private static final String PLURAL_INDICATOR_LOWERCASE = "s";

    TaskDto(ProgressDto progress, String id, String name, long migrationEstimateSeconds) {
        this.progress = progress;
        this.id = id;
        this.name = name;
        this.migrationEstimateSeconds = migrationEstimateSeconds;
    }

    public ProgressDto getProgress() {
        return this.progress;
    }

    public String getId() {
        return this.id;
    }

    public long getMigrationEstimateSeconds() {
        return this.migrationEstimateSeconds;
    }

    public abstract Task toInternalType();

    public boolean isSuccessful() {
        return this.progress != null && Objects.equals((Object)this.progress.getStatus(), (Object)ProgressDto.Status.FINISHED);
    }

    public abstract TaskType getTaskType();

    public String getTaskTypeSingularTitleCase() {
        String taskType = WordUtils.capitalizeFully((String)this.getTaskType().name());
        if (taskType.endsWith(PLURAL_INDICATOR_LOWERCASE)) {
            taskType = taskType.substring(0, taskType.length() - 1);
        }
        return taskType;
    }
}

