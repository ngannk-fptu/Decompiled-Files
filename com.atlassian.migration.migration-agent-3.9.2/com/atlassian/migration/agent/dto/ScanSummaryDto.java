/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.migration.agent.dto.DuplicateEmailsConfigDto;
import com.atlassian.migration.agent.dto.InvalidEmailsConfigDto;
import com.atlassian.migration.agent.entity.ScanStatus;
import java.time.Instant;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ScanSummaryDto {
    @JsonProperty
    private final ScanStatus status;
    @JsonProperty
    private final Instant startedAt;
    @JsonProperty
    private final Instant finishedAt;
    @JsonProperty
    private final Long invalidUsersCount;
    @JsonProperty
    private final Long duplicatedUsersCount;
    @JsonProperty
    private final String scanId;
    @JsonProperty
    private final DuplicateEmailsConfigDto duplicatedEmailsConfig;
    @JsonProperty
    private final InvalidEmailsConfigDto invalidEmailsConfig;

    @JsonCreator
    public ScanSummaryDto(@JsonProperty(value="status") ScanStatus status, @JsonProperty(value="startedAt") Instant startedAt, @JsonProperty(value="finishedAt") Instant finishedAt, @JsonProperty(value="invalidUsersCount") Long invalidUsersCount, @JsonProperty(value="duplicatedUsersCount") Long duplicatedUsersCount, @JsonProperty(value="scanId") String scanId, @JsonProperty(value="duplicatedEmailsConfig") DuplicateEmailsConfigDto duplicatedEmailsConfig, @JsonProperty(value="invalidEmailsConfig") InvalidEmailsConfigDto invalidEmailsConfig) {
        this.status = status;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.invalidUsersCount = invalidUsersCount;
        this.duplicatedUsersCount = duplicatedUsersCount;
        this.scanId = scanId;
        this.duplicatedEmailsConfig = duplicatedEmailsConfig;
        this.invalidEmailsConfig = invalidEmailsConfig;
    }

    @JsonCreator
    public ScanSummaryDto(@JsonProperty(value="duplicatedEmailsConfig") DuplicateEmailsConfigDto duplicatedEmailsConfig, @JsonProperty(value="invalidEmailsConfig") InvalidEmailsConfigDto invalidEmailsConfig) {
        this.duplicatedEmailsConfig = duplicatedEmailsConfig;
        this.invalidEmailsConfig = invalidEmailsConfig;
        this.status = null;
        this.startedAt = null;
        this.finishedAt = null;
        this.invalidUsersCount = null;
        this.duplicatedUsersCount = null;
        this.scanId = null;
    }

    public ScanStatus getStatus() {
        return this.status;
    }

    public Instant getStartedAt() {
        return this.startedAt;
    }

    public Instant getFinishedAt() {
        return this.finishedAt;
    }

    public Long getInvalidUsersCount() {
        return this.invalidUsersCount;
    }

    public Long getDuplicatedUsersCount() {
        return this.duplicatedUsersCount;
    }

    public String getScanId() {
        return this.scanId;
    }

    public DuplicateEmailsConfigDto getDuplicatedEmailsConfig() {
        return this.duplicatedEmailsConfig;
    }

    public InvalidEmailsConfigDto getInvalidEmailsConfig() {
        return this.invalidEmailsConfig;
    }
}

