/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupComparator
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.group.InternalDirectoryGroup
 *  com.google.errorprone.annotations.Immutable
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupComparator;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import com.google.errorprone.annotations.Immutable;
import java.io.Serializable;
import java.util.Date;
import javax.annotation.Nullable;

@Immutable
public class CachedCrowdInternalDirectoryGroup
implements InternalDirectoryGroup,
Serializable {
    private final GroupType type;
    private final boolean active;
    private final String description;
    private final long directoryId;
    private final String name;
    private final boolean local;
    private final Date createdDate;
    private final Date updatedDate;
    private final String externalId;

    public CachedCrowdInternalDirectoryGroup(InternalDirectoryGroup group) {
        this.type = group.getType();
        this.active = group.isActive();
        this.description = group.getDescription();
        this.directoryId = group.getDirectoryId();
        this.name = group.getName();
        this.local = group.isLocal();
        this.createdDate = group.getCreatedDate() == null ? null : new Date(group.getCreatedDate().getTime());
        this.updatedDate = group.getUpdatedDate() == null ? null : new Date(group.getUpdatedDate().getTime());
        this.externalId = group.getExternalId();
    }

    public boolean isLocal() {
        return this.local;
    }

    public Date getCreatedDate() {
        if (this.createdDate != null) {
            return new Date(this.createdDate.getTime());
        }
        return null;
    }

    public Date getUpdatedDate() {
        if (this.updatedDate != null) {
            return new Date(this.updatedDate.getTime());
        }
        return null;
    }

    public int compareTo(Group other) {
        return GroupComparator.compareTo((Group)this, (Group)other);
    }

    public GroupType getType() {
        return this.type;
    }

    public boolean isActive() {
        return this.active;
    }

    public String getDescription() {
        return this.description;
    }

    @Nullable
    public String getExternalId() {
        return this.externalId;
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    public String getName() {
        return this.name;
    }
}

