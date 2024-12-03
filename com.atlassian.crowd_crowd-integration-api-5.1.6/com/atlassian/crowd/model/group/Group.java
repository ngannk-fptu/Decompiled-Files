/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.group;

import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.group.GroupType;
import javax.annotation.Nullable;

public interface Group
extends DirectoryEntity,
Comparable<Group> {
    public GroupType getType();

    public boolean isActive();

    public String getDescription();

    @Nullable
    public String getExternalId();
}

