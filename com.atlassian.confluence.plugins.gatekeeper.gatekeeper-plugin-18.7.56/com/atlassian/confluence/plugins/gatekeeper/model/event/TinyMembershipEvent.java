/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.event;

import com.atlassian.confluence.plugins.gatekeeper.model.event.EventType;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyEvent;

public class TinyMembershipEvent
extends TinyEvent {
    private static final long serialVersionUID = 3949145755045481060L;
    private final String groupName;

    protected TinyMembershipEvent(EventType eventType, String groupName) {
        super(eventType);
        this.groupName = groupName;
    }

    public static TinyMembershipEvent addedUsers(String groupName) {
        return new TinyMembershipEvent(EventType.USER_MEMBERSHIP_ADDED, groupName);
    }

    public static TinyMembershipEvent deletedUsers(String groupName) {
        return new TinyMembershipEvent(EventType.USER_MEMBERSHIP_DELETED, groupName);
    }

    public static TinyMembershipEvent addedGroups(String groupName) {
        return new TinyMembershipEvent(EventType.GROUP_MEMBERSHIP_ADDED, groupName);
    }

    public static TinyMembershipEvent deletedGroups(String groupName) {
        return new TinyMembershipEvent(EventType.GROUP_MEMBERSHIP_DELETED, groupName);
    }

    public String getGroupName() {
        return this.groupName;
    }

    public String toString() {
        return this.eventType + "{groupName='" + this.groupName + "'}";
    }
}

