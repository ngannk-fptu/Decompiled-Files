/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.ImmutableGroup
 */
package com.atlassian.crowd.manager.permission;

import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.ImmutableGroup;

public abstract class GroupAdministrationMapping {
    protected final ImmutableGroup targetGroup;

    public GroupAdministrationMapping(Group targetGroup) {
        this.targetGroup = ImmutableGroup.from((Group)targetGroup);
    }

    public Group getTargetGroup() {
        return this.targetGroup;
    }
}

