/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.DiscriminatorValue
 *  javax.persistence.Entity
 *  javax.persistence.EnumType
 *  javax.persistence.Enumerated
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.GlobalEntityType;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.entity.TaskType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@DiscriminatorValue(value="GLOBAL_ENTITIES")
public class MigrateGlobalEntitiesTask
extends Task {
    @Column(name="globalEntityType")
    @Enumerated(value=EnumType.STRING)
    protected GlobalEntityType globalEntityType;

    public MigrateGlobalEntitiesTask() {
    }

    public MigrateGlobalEntitiesTask(GlobalEntityType globalEntityType) {
        this.globalEntityType = globalEntityType;
    }

    public MigrateGlobalEntitiesTask(MigrateGlobalEntitiesTask migrateGlobalEntitiesTask) {
        super(migrateGlobalEntitiesTask.getWeight(), migrateGlobalEntitiesTask.getIndex());
        this.globalEntityType = migrateGlobalEntitiesTask.globalEntityType;
    }

    @Override
    public Task copy() {
        return new MigrateGlobalEntitiesTask(this);
    }

    @Override
    public String getName() {
        return "Migrate global templates";
    }

    @Override
    public TaskType getType() {
        return TaskType.GLOBAL_ENTITIES;
    }

    public GlobalEntityType getGlobalEntityType() {
        return this.globalEntityType;
    }
}

