/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.DiscriminatorValue
 *  javax.persistence.Entity
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.AbstractSpaceTask;
import com.atlassian.migration.agent.entity.TaskType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value="SPACE")
public class ConfluenceSpaceTask
extends AbstractSpaceTask {
    @Override
    public ConfluenceSpaceTask copy() {
        return new ConfluenceSpaceTask(this);
    }

    public ConfluenceSpaceTask() {
    }

    public ConfluenceSpaceTask(ConfluenceSpaceTask spaceTask) {
        super(spaceTask);
    }

    @Override
    public String getName() {
        return String.format("Migrate space %s", this.getSpaceKey());
    }

    @Override
    public TaskType getType() {
        return TaskType.SPACE;
    }
}

