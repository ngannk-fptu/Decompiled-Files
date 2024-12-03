/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.JoinColumn
 *  javax.persistence.ManyToOne
 *  javax.persistence.Table
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.MigrateAppsTask;
import com.atlassian.migration.agent.entity.WithId;
import java.time.Instant;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="MIG_NEEDED_IN_CLOUD_APP")
public class NeededInCloudApp
extends WithId {
    @ManyToOne
    @JoinColumn(name="taskId", nullable=false)
    private MigrateAppsTask task;
    @Column(name="appKey", nullable=false)
    private String appKey;
    @Column(name="created", nullable=false)
    private Instant created;

    public NeededInCloudApp() {
    }

    public NeededInCloudApp(MigrateAppsTask task, String appKey) {
        this.created = Instant.now();
        this.task = task;
        this.appKey = appKey;
    }

    public MigrateAppsTask getTask() {
        return this.task;
    }

    public void setTask(MigrateAppsTask task) {
        this.task = task;
    }

    public String getAppKey() {
        return this.appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public Instant getCreated() {
        return this.created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NeededInCloudApp other = (NeededInCloudApp)o;
        return Objects.equals(this.appKey, other.appKey) && Objects.equals(this.task, other.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.appKey, this.task);
    }
}

