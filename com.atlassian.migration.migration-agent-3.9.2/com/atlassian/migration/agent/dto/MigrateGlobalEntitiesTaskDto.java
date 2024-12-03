/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.migration.agent.dto.ProgressDto;
import com.atlassian.migration.agent.dto.TaskDto;
import com.atlassian.migration.agent.entity.GlobalEntityType;
import com.atlassian.migration.agent.entity.MigrateGlobalEntitiesTask;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.entity.TaskType;
import com.atlassian.migration.agent.json.JsonType;
import javax.annotation.Nullable;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonType(value="global-entities")
public class MigrateGlobalEntitiesTaskDto
extends TaskDto {
    @JsonProperty
    protected final GlobalEntityType globalEntityType;
    @Nullable
    @JsonProperty
    protected final Long numberOfGlobalPageTemplatesExported;
    @Nullable
    @JsonProperty
    protected final Long numberOfEditedSystemTemplatesExported;

    @JsonCreator
    public MigrateGlobalEntitiesTaskDto(@JsonProperty(value="id") String id, @JsonProperty(value="name") String name, @JsonProperty(value="migrationEstimateSeconds") long migrationEstimateSeconds, @JsonProperty(value="progress") ProgressDto progress, @JsonProperty(value="globalEntityType") GlobalEntityType globalEntityType, @Nullable @JsonProperty(value="numberOfGlobalPageTemplatesExported") Long numberOfGlobalPageTemplatesExported, @Nullable @JsonProperty(value="numberOfEditedSystemTemplatesExported") Long numberOfEditedSystemTemplatesExported) {
        super(progress, id, name, migrationEstimateSeconds);
        this.globalEntityType = globalEntityType;
        this.numberOfGlobalPageTemplatesExported = numberOfGlobalPageTemplatesExported;
        this.numberOfEditedSystemTemplatesExported = numberOfEditedSystemTemplatesExported;
    }

    @Override
    public Task toInternalType() {
        return new MigrateGlobalEntitiesTask(this.globalEntityType);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.GLOBAL_ENTITIES;
    }

    public GlobalEntityType getGlobalEntityType() {
        return this.globalEntityType;
    }

    @Nullable
    @Generated
    public Long getNumberOfGlobalPageTemplatesExported() {
        return this.numberOfGlobalPageTemplatesExported;
    }

    @Nullable
    @Generated
    public Long getNumberOfEditedSystemTemplatesExported() {
        return this.numberOfEditedSystemTemplatesExported;
    }
}

