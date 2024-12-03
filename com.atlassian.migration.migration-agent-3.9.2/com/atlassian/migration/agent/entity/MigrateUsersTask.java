/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.DiscriminatorValue
 *  javax.persistence.Entity
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.entity.TaskType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value="USERS")
public class MigrateUsersTask
extends Task {
    @Column(name="scoped")
    protected Boolean scoped;

    public MigrateUsersTask() {
        this(false);
    }

    public MigrateUsersTask(boolean scoped) {
        this.scoped = scoped;
    }

    public MigrateUsersTask(MigrateUsersTask usersTask) {
        super(usersTask.getWeight(), usersTask.getIndex());
        this.scoped = usersTask.scoped;
    }

    @Override
    public Task copy() {
        return new MigrateUsersTask(this);
    }

    @Override
    public String getName() {
        return "Migrate users and groups";
    }

    @Override
    public TaskType getType() {
        return TaskType.USERS;
    }

    public Boolean isScoped() {
        if (this.scoped != null) {
            return this.scoped;
        }
        return Boolean.FALSE;
    }

    public void setScoped(Boolean scoped) {
        this.scoped = scoped;
    }
}

