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

import com.atlassian.migration.agent.dto.CloudSiteDto;
import com.atlassian.migration.agent.dto.ProgressDto;
import com.atlassian.migration.agent.dto.TaskDto;
import com.atlassian.migration.agent.entity.MigrationTag;
import com.atlassian.migration.agent.entity.PlanActiveStatus;
import com.atlassian.migration.agent.mapi.MigrationCreator;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class PlanDto {
    @JsonProperty
    private final String id;
    @JsonProperty
    private final String checkExecutionId;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final Instant createdTime;
    @JsonProperty
    private final Instant lastUpdate;
    @JsonProperty
    private final CloudSiteDto cloudSite;
    @JsonProperty
    private final List<TaskDto> tasks;
    @JsonProperty
    private final ProgressDto progress;
    @JsonProperty
    private final List<String> preflightChecksToOverride;
    @JsonProperty
    private final PlanActiveStatus activeStatus;
    @JsonProperty
    private final String migrationId;
    @JsonProperty
    private final MigrationTag migrationTag;
    @JsonProperty
    private final MigrationCreator migrationCreator;

    @JsonCreator
    public PlanDto(@JsonProperty(value="id") String id, @JsonProperty(value="checkExecutionId") String checkExecutionId, @JsonProperty(value="name") String name, @JsonProperty(value="createdTime") Instant createdTime, @JsonProperty(value="lastUpdate") Instant lastUpdate, @JsonProperty(value="cloudSite") CloudSiteDto cloudSite, @JsonProperty(value="tasks") List<TaskDto> tasks, @JsonProperty(value="progress") ProgressDto progress, @Nullable @JsonProperty(value="overriddenPreflightCheckTypes") List<String> preflightChecksToOverride, @JsonProperty(value="activeStatus") PlanActiveStatus activeStatus, @Nullable @JsonProperty(value="migrationId") String migrationId, @Nullable @JsonProperty(value="migrationTag") MigrationTag migrationTag, @Nullable @JsonProperty(value="migrationCreator") MigrationCreator migrationCreator) {
        this.id = id;
        this.checkExecutionId = checkExecutionId;
        this.name = name;
        this.createdTime = createdTime;
        this.lastUpdate = lastUpdate;
        this.cloudSite = cloudSite;
        this.tasks = Collections.unmodifiableList(tasks);
        this.progress = progress;
        this.preflightChecksToOverride = preflightChecksToOverride;
        this.activeStatus = activeStatus;
        this.migrationId = migrationId;
        this.migrationTag = migrationTag == null ? MigrationTag.NOT_SPECIFIED : migrationTag;
        this.migrationCreator = migrationCreator == null ? MigrationCreator.CCMA : migrationCreator;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public String getCheckExecutionId() {
        return this.checkExecutionId;
    }

    public Instant getLastUpdate() {
        return this.lastUpdate;
    }

    public Instant getCreatedTime() {
        return this.createdTime;
    }

    public CloudSiteDto getCloudSite() {
        return this.cloudSite;
    }

    public List<TaskDto> getTasks() {
        return this.tasks;
    }

    public List<String> getPreflightChecksToOverride() {
        return this.preflightChecksToOverride;
    }

    public ProgressDto getProgress() {
        return this.progress;
    }

    public boolean hasFinishedExecuting() {
        return ProgressDto.Status.finishedStatus().contains((Object)this.progress.getStatus());
    }

    public PlanActiveStatus getActiveStatus() {
        return this.activeStatus;
    }

    public String getMigrationId() {
        return this.migrationId;
    }

    public MigrationTag getMigrationTag() {
        return this.migrationTag;
    }

    public MigrationCreator getMigrationCreator() {
        return this.migrationCreator;
    }

    @Generated
    public static PlanDtoBuilder builder() {
        return new PlanDtoBuilder();
    }

    @Generated
    public static class PlanDtoBuilder {
        @Generated
        private String id;
        @Generated
        private String checkExecutionId;
        @Generated
        private String name;
        @Generated
        private Instant createdTime;
        @Generated
        private Instant lastUpdate;
        @Generated
        private CloudSiteDto cloudSite;
        @Generated
        private ArrayList<TaskDto> tasks;
        @Generated
        private ProgressDto progress;
        @Generated
        private List<String> preflightChecksToOverride;
        @Generated
        private PlanActiveStatus activeStatus;
        @Generated
        private String migrationId;
        @Generated
        private MigrationTag migrationTag;
        @Generated
        private MigrationCreator migrationCreator;

        @Generated
        PlanDtoBuilder() {
        }

        @Generated
        public PlanDtoBuilder id(String id) {
            this.id = id;
            return this;
        }

        @Generated
        public PlanDtoBuilder checkExecutionId(String checkExecutionId) {
            this.checkExecutionId = checkExecutionId;
            return this;
        }

        @Generated
        public PlanDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        @Generated
        public PlanDtoBuilder createdTime(Instant createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        @Generated
        public PlanDtoBuilder lastUpdate(Instant lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }

        @Generated
        public PlanDtoBuilder cloudSite(CloudSiteDto cloudSite) {
            this.cloudSite = cloudSite;
            return this;
        }

        @Generated
        public PlanDtoBuilder task(TaskDto task) {
            if (this.tasks == null) {
                this.tasks = new ArrayList();
            }
            this.tasks.add(task);
            return this;
        }

        @Generated
        public PlanDtoBuilder tasks(Collection<? extends TaskDto> tasks) {
            if (tasks == null) {
                throw new NullPointerException("tasks cannot be null");
            }
            if (this.tasks == null) {
                this.tasks = new ArrayList();
            }
            this.tasks.addAll(tasks);
            return this;
        }

        @Generated
        public PlanDtoBuilder clearTasks() {
            if (this.tasks != null) {
                this.tasks.clear();
            }
            return this;
        }

        @Generated
        public PlanDtoBuilder progress(ProgressDto progress) {
            this.progress = progress;
            return this;
        }

        @Generated
        public PlanDtoBuilder preflightChecksToOverride(List<String> preflightChecksToOverride) {
            this.preflightChecksToOverride = preflightChecksToOverride;
            return this;
        }

        @Generated
        public PlanDtoBuilder activeStatus(PlanActiveStatus activeStatus) {
            this.activeStatus = activeStatus;
            return this;
        }

        @Generated
        public PlanDtoBuilder migrationId(String migrationId) {
            this.migrationId = migrationId;
            return this;
        }

        @Generated
        public PlanDtoBuilder migrationTag(MigrationTag migrationTag) {
            this.migrationTag = migrationTag;
            return this;
        }

        @Generated
        public PlanDtoBuilder migrationCreator(MigrationCreator migrationCreator) {
            this.migrationCreator = migrationCreator;
            return this;
        }

        @Generated
        public PlanDto build() {
            List<TaskDto> tasks;
            switch (this.tasks == null ? 0 : this.tasks.size()) {
                case 0: {
                    tasks = Collections.emptyList();
                    break;
                }
                case 1: {
                    tasks = Collections.singletonList(this.tasks.get(0));
                    break;
                }
                default: {
                    tasks = Collections.unmodifiableList(new ArrayList<TaskDto>(this.tasks));
                }
            }
            return new PlanDto(this.id, this.checkExecutionId, this.name, this.createdTime, this.lastUpdate, this.cloudSite, tasks, this.progress, this.preflightChecksToOverride, this.activeStatus, this.migrationId, this.migrationTag, this.migrationCreator);
        }

        @Generated
        public String toString() {
            return "PlanDto.PlanDtoBuilder(id=" + this.id + ", checkExecutionId=" + this.checkExecutionId + ", name=" + this.name + ", createdTime=" + this.createdTime + ", lastUpdate=" + this.lastUpdate + ", cloudSite=" + this.cloudSite + ", tasks=" + this.tasks + ", progress=" + this.progress + ", preflightChecksToOverride=" + this.preflightChecksToOverride + ", activeStatus=" + (Object)((Object)this.activeStatus) + ", migrationId=" + this.migrationId + ", migrationTag=" + (Object)((Object)this.migrationTag) + ", migrationCreator=" + (Object)((Object)this.migrationCreator) + ")";
        }
    }
}

