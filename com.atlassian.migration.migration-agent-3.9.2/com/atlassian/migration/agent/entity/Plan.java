/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.CascadeType
 *  javax.persistence.Column
 *  javax.persistence.Embedded
 *  javax.persistence.Entity
 *  javax.persistence.EnumType
 *  javax.persistence.Enumerated
 *  javax.persistence.JoinColumn
 *  javax.persistence.ManyToOne
 *  javax.persistence.OneToMany
 *  javax.persistence.OrderColumn
 *  javax.persistence.Table
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.dto.util.UserMigrationType;
import com.atlassian.migration.agent.entity.AbstractSpaceTask;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.entity.GlobalEntityType;
import com.atlassian.migration.agent.entity.MigrateGlobalEntitiesTask;
import com.atlassian.migration.agent.entity.MigrateUsersTask;
import com.atlassian.migration.agent.entity.MigrationTag;
import com.atlassian.migration.agent.entity.PlanActiveStatus;
import com.atlassian.migration.agent.entity.PlanSchedulerVersion;
import com.atlassian.migration.agent.entity.Progress;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.entity.WithId;
import com.atlassian.migration.agent.mapi.MigrationCreator;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

@Entity
@Table(name="MIG_PLAN")
public class Plan
extends WithId {
    @OneToMany(mappedBy="plan", cascade={CascadeType.ALL})
    @OrderColumn(name="taskIndex")
    private List<Task> tasks;
    @ManyToOne
    @JoinColumn(name="cloudId")
    private CloudSite cloudSite;
    @Column(name="planName", nullable=false, unique=true)
    private String name;
    @Column(name="migrationId")
    private String migrationId;
    @Column(name="migrationScopeId")
    private String migrationScopeId;
    @Column(name="activeStatus", nullable=false)
    @Enumerated(value=EnumType.STRING)
    private PlanActiveStatus activeStatus;
    @Column(name="createdTime", nullable=false)
    private Instant createdTime;
    @Column(name="lastUpdate", nullable=false)
    private Instant lastUpdate;
    @Column(name="schedulerVersion")
    @Enumerated(value=EnumType.STRING)
    private PlanSchedulerVersion schedulerVersion;
    @Column(name="migrationTag", nullable=false)
    @Enumerated(value=EnumType.STRING)
    private MigrationTag migrationTag;
    @Column(name="migrationCreator")
    @Enumerated(value=EnumType.STRING)
    private MigrationCreator migrationCreator;
    @Embedded
    private Progress progress = Progress.created();

    public Plan() {
    }

    public Plan(Plan other) {
        this.tasks = other.getTasks().stream().map(Task::copy).collect(Collectors.toList());
        this.tasks.forEach(task -> task.setPlan(this));
        this.cloudSite = other.cloudSite;
        this.name = other.name;
        this.activeStatus = PlanActiveStatus.ACTIVE;
        this.lastUpdate = this.createdTime = Instant.now();
        this.progress = Progress.created();
        this.migrationTag = other.migrationTag;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMigrationId() {
        return this.migrationId;
    }

    public void setMigrationId(String migrationId) {
        this.migrationId = migrationId;
    }

    public Instant getLastUpdate() {
        return this.lastUpdate;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Instant getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }

    public List<Task> getTasks() {
        return this.tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public CloudSite getCloudSite() {
        return this.cloudSite;
    }

    public void setCloudSite(CloudSite cloudSite) {
        this.cloudSite = cloudSite;
    }

    public Progress getProgress() {
        return this.progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    public String getMigrationScopeId() {
        return this.migrationScopeId;
    }

    public void setMigrationScopeId(String migrationScopeId) {
        this.migrationScopeId = migrationScopeId;
    }

    public PlanActiveStatus getActiveStatus() {
        return this.activeStatus;
    }

    public void setActiveStatus(PlanActiveStatus activeStatus) {
        this.activeStatus = activeStatus;
    }

    public PlanSchedulerVersion getSchedulerVersion() {
        return this.schedulerVersion;
    }

    public void setSchedulerVersion(PlanSchedulerVersion schedulerVersion) {
        this.schedulerVersion = schedulerVersion;
    }

    public MigrationTag getMigrationTag() {
        return this.migrationTag;
    }

    public void setMigrationTag(MigrationTag migrationTag) {
        this.migrationTag = migrationTag;
    }

    public MigrationCreator getMigrationCreator() {
        return this.migrationCreator;
    }

    public void setMigrationCreator(MigrationCreator migrationCreator) {
        this.migrationCreator = migrationCreator;
    }

    public Set<String> getSpaceKeysBasedOnUserTaskInPlan() {
        Optional<MigrateUsersTask> maybeUserTask = this.getUserTaskOfPlan();
        if (maybeUserTask.isPresent() && maybeUserTask.get().isScoped().booleanValue()) {
            return this.getTasks().stream().filter(task -> task instanceof AbstractSpaceTask).map(task -> ((AbstractSpaceTask)task).getSpaceKey()).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    public UserMigrationType getUserMigrationTypeBasedOnUserTaskInPlan() {
        Optional<MigrateUsersTask> maybeUserTask = this.getUserTaskOfPlan();
        if (maybeUserTask.isPresent()) {
            if (maybeUserTask.get().isScoped().booleanValue()) {
                return UserMigrationType.SCOPED;
            }
            return UserMigrationType.ALL;
        }
        return UserMigrationType.NONE;
    }

    public Optional<MigrateUsersTask> getUserTaskOfPlan() {
        return this.getTasks().stream().filter(MigrateUsersTask.class::isInstance).map(MigrateUsersTask.class::cast).findFirst();
    }

    public Optional<MigrateGlobalEntitiesTask> getGlobalEntitiesTaskOfPlan() {
        return this.getTasks().stream().filter(MigrateGlobalEntitiesTask.class::isInstance).map(MigrateGlobalEntitiesTask.class::cast).findFirst();
    }

    public Optional<GlobalEntityType> getGlobalEntityTaskOfPlan() {
        return this.getTasks().stream().filter(MigrateGlobalEntitiesTask.class::isInstance).map(MigrateGlobalEntitiesTask.class::cast).findFirst().map(MigrateGlobalEntitiesTask::getGlobalEntityType);
    }
}

