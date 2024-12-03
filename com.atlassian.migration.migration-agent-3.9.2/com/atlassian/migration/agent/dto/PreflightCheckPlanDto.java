/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.migration.agent.dto.AppsProgressDto;
import com.atlassian.migration.agent.dto.CloudSiteDto;
import com.atlassian.migration.agent.dto.MigrationDto;
import com.atlassian.migration.agent.dto.PlanDto;
import com.atlassian.migration.agent.dto.PreflightCheckProgressDto;
import com.atlassian.migration.agent.dto.ProgressDto;
import com.atlassian.migration.agent.dto.TaskDto;
import com.atlassian.migration.agent.entity.MigrationTag;
import com.atlassian.migration.agent.entity.PlanActiveStatus;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public class PreflightCheckPlanDto {
    @JsonProperty
    public final String id;
    @JsonProperty
    public final String checkExecutionId;
    @JsonProperty
    public final String name;
    @JsonProperty
    public final Instant lastUpdate;
    @JsonProperty
    public final CloudSiteDto cloudSite;
    @JsonProperty
    public final List<TaskDto> tasks;
    @JsonProperty
    public final PreflightCheckProgressDto progress;
    @JsonProperty
    public final List<String> preflightChecksToOverride;
    @JsonProperty
    public final AppsProgressDto appsProgress;
    @JsonProperty
    public final PlanActiveStatus activeStatus;
    @JsonProperty
    public final MigrationDto migrationStatus;
    @JsonProperty
    public final MigrationTag migrationTag;

    public PreflightCheckPlanDto(PlanDto planDto, List<CheckResultDto> checks, AppsProgressDto appsProgress, MigrationDto migrationStatus) {
        this.id = planDto.getId();
        this.checkExecutionId = planDto.getCheckExecutionId();
        this.name = planDto.getName();
        this.lastUpdate = planDto.getLastUpdate();
        this.cloudSite = planDto.getCloudSite();
        this.tasks = planDto.getTasks();
        this.progress = PreflightCheckPlanDto.populateProgress(planDto.getProgress(), checks, migrationStatus);
        this.preflightChecksToOverride = planDto.getPreflightChecksToOverride() != null ? planDto.getPreflightChecksToOverride() : Collections.emptyList();
        this.appsProgress = appsProgress;
        this.activeStatus = planDto.getActiveStatus();
        this.migrationStatus = migrationStatus;
        this.migrationTag = planDto.getMigrationTag();
    }

    private static PreflightCheckProgressDto populateProgress(ProgressDto progressDto, List<CheckResultDto> checks, MigrationDto migrationStatus) {
        return new PreflightCheckProgressDto(progressDto, checks, migrationStatus);
    }
}

