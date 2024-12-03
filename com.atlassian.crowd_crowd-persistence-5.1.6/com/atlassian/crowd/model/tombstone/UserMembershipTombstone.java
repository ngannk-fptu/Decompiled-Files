/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.tombstone;

import com.atlassian.crowd.model.tombstone.MembershipTombstone;

public class UserMembershipTombstone
extends MembershipTombstone {
    protected UserMembershipTombstone() {
    }

    public UserMembershipTombstone(long timestamp, String childName, String parentName, long directoryId) {
        super(timestamp, childName, parentName, directoryId);
    }
}

