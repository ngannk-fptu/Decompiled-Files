/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.migration.agent.dto.MigrationDto;
import com.atlassian.migration.agent.dto.ProgressDto;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public class PreflightCheckProgressDto
extends ProgressDto {
    @JsonProperty
    public final List<CheckResultDto> checks;
    @JsonProperty
    public final MigrationDto migrationStatus;

    public PreflightCheckProgressDto(ProgressDto progressDto, List<CheckResultDto> checks, MigrationDto migrationStatus) {
        super(progressDto.getCompletionPercent(), progressDto.getStatus(), progressDto.getMessage(), progressDto.getStartTime(), progressDto.getEndTime(), progressDto.getDetailedStatus());
        this.checks = checks;
        this.migrationStatus = migrationStatus;
    }
}

