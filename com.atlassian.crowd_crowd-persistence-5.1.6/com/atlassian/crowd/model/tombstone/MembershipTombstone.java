/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.model.tombstone;

import com.atlassian.crowd.model.tombstone.AbstractTombstone;
import com.google.common.base.MoreObjects;

public class MembershipTombstone
extends AbstractTombstone {
    private String childName;
    private String parentName;
    private long directoryId;

    protected MembershipTombstone() {
    }

    protected MembershipTombstone(long timestamp, String childName, String parentName, long directoryId) {
        super(timestamp);
        this.childName = childName;
        this.parentName = parentName;
        this.directoryId = directoryId;
    }

    public String getChildName() {
        return this.childName;
    }

    protected void setChildName(String childName) {
        this.childName = childName;
    }

    public String getParentName() {
        return this.parentName;
    }

    protected void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    protected void setDirectoryId(long directoryId) {
        this.directoryId = directoryId;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("childName", (Object)this.childName).add("parentName", (Object)this.parentName).add("directoryId", this.directoryId).toString();
    }
}

