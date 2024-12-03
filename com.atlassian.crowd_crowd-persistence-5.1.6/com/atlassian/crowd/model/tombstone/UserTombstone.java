/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.user.ImmutableUser
 *  com.atlassian.crowd.model.user.User
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.model.tombstone;

import com.atlassian.crowd.model.tombstone.AbstractTombstone;
import com.atlassian.crowd.model.user.ImmutableUser;
import com.atlassian.crowd.model.user.User;
import com.google.common.base.MoreObjects;

public class UserTombstone
extends AbstractTombstone {
    private String name;
    private long directoryId;

    protected UserTombstone() {
    }

    public UserTombstone(long timestamp, String name, long directoryId) {
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

    public User toUser() {
        return new ImmutableUser(this.getDirectoryId(), this.getName(), null, null, false, null, null, null);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("name", (Object)this.name).add("directoryId", this.directoryId).toString();
    }
}

