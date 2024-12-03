/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.SpaceType
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.confluence.api.model.content.SpaceType;
import com.atlassian.migration.agent.dto.ProgressDto;
import com.atlassian.migration.agent.model.stats.ContentSummary;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.codehaus.jackson.annotate.JsonProperty;

@ParametersAreNonnullByDefault
public class SpaceDto {
    @JsonProperty
    private final String key;
    @JsonProperty
    private final long id;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final ContentSummary contentSummary;
    @JsonProperty
    private final ProgressDto progress;
    @JsonProperty
    private final Long migrationEstimateSeconds;
    @JsonProperty
    private final SpaceType spaceType;

    public SpaceDto(String key, long id, String name, @Nullable ContentSummary contentSummary, @Nullable ProgressDto progress, Long durationEstimationSeconds, @Nullable SpaceType spaceType) {
        this.key = key;
        this.id = id;
        this.name = name;
        this.contentSummary = contentSummary;
        this.progress = progress;
        this.migrationEstimateSeconds = durationEstimationSeconds;
        this.spaceType = spaceType;
    }

    public String getKey() {
        return this.key;
    }

    public long getId() {
        return this.id;
    }

    public ContentSummary getContentSummary() {
        return this.contentSummary;
    }

    public Long getMigrationEstimateSeconds() {
        return this.migrationEstimateSeconds;
    }

    public SpaceType getSpaceType() {
        return this.spaceType;
    }
}

