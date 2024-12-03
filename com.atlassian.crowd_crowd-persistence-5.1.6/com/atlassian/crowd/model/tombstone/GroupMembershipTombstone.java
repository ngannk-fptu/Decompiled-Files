/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.tombstone;

import com.atlassian.crowd.model.tombstone.MembershipTombstone;

public class GroupMembershipTombstone
extends MembershipTombstone {
    protected GroupMembershipTombstone() {
    }

    public GroupMembershipTombstone(long timestamp, String childName, String parentName, long directoryId) {
        super(timestamp, childName, parentName, directoryId);
    }
}

