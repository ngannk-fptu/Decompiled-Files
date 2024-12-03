/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.membership.MembershipType
 */
package com.atlassian.crowd.event.group;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.group.GroupMembershipDeletedEvent;
import com.atlassian.crowd.model.membership.MembershipType;

public class AutoGroupMembershipDeletedEvent
extends GroupMembershipDeletedEvent {
    public AutoGroupMembershipDeletedEvent(Object source, Directory directory, String entityName, String groupName, MembershipType membershipType) {
        super(source, directory, entityName, groupName, membershipType);
    }
}

