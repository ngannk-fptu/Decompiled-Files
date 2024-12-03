/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.DiscriminatorColumn
 *  javax.persistence.Embedded
 *  javax.persistence.Entity
 *  javax.persistence.Inheritance
 *  javax.persistence.InheritanceType
 *  javax.persistence.JoinColumn
 *  javax.persistence.ManyToOne
 *  javax.persistence.Table
 *  org.hibernate.annotations.ColumnDefault
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.Progress;
import com.atlassian.migration.agent.entity.TaskType;
import com.atlassian.migration.agent.entity.WithId;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name="MIG_TASK")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="taskType")
public abstract class Task
extends WithId {
    @ManyToOne
    @JoinColumn(name="planId", nullable=false)
    private Plan plan;
    @Embedded
    private Progress progress = Progress.created();
    @Column(name="taskIndex", insertable=false, updatable=false)
    private int index;
    @Column(name="weight")
    @ColumnDefault(value="1")
    private int weight = 1;
    @Column(name="containerId")
    private String containerId;

    public abstract Task copy();

    public Task() {
    }

    protected Task(Task task) {
        this(task.weight, task.index);
    }

    public Task(int weight, int index) {
        this.weight = weight;
        this.index = index;
    }

    public Plan getPlan() {
        return this.plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public Progress getProgress() {
        return this.progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    public int getIndex() {
        return this.index;
    }

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getContainerId() {
        return this.containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public abstract String getName();

    public abstract TaskType getType();

    public String getAnalyticsEventType() {
        return this.getType().getAnalyticsEvent();
    }
}

