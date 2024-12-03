/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.ImmutableGroup
 */
package com.atlassian.crowd.model.tombstone;

import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.ImmutableGroup;
import com.atlassian.crowd.model.tombstone.AbstractTombstone;

public class GroupTombstone
extends AbstractTombstone {
    private String name;
    private long directoryId;

    protected GroupTombstone() {
    }

    public GroupTombstone(long timestamp, String name, long directoryId) {
        super(timestamp);
        this.name = name;
        this.directoryId = directoryId;
    }

    public String getName() {
        return this.name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    protected void setDirectoryId(long directoryId) {
        this.directoryId = directoryId;
    }

    public Group toGroup() {
        return ImmutableGroup.builder((long)this.getDirectoryId(), (String)this.getName()).build();
    }
}

