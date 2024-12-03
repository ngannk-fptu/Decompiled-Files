/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.Task;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public abstract class AbstractSpaceTask
extends Task {
    @Column(name="spaceKey")
    protected String spaceKey;

    public AbstractSpaceTask() {
    }

    public AbstractSpaceTask(AbstractSpaceTask spaceTask) {
        super(spaceTask.getWeight(), spaceTask.getIndex());
        this.spaceKey = spaceTask.spaceKey;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }
}

