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
@DiscriminatorValue(value="ATTACHMENTS")
public class SpaceAttachmentsOnlyTask
extends AbstractSpaceTask {
    @Override
    public SpaceAttachmentsOnlyTask copy() {
        return new SpaceAttachmentsOnlyTask(this);
    }

    public SpaceAttachmentsOnlyTask() {
    }

    public SpaceAttachmentsOnlyTask(SpaceAttachmentsOnlyTask attachmentsOnlyTask) {
        super(attachmentsOnlyTask);
    }

    @Override
    public String getName() {
        return String.format("Migrate attachments only for space %s", this.getSpaceKey());
    }

    @Override
    public TaskType getType() {
        return TaskType.ATTACHMENTS;
    }
}

