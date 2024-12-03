/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.event;

import com.atlassian.confluence.plugins.gatekeeper.model.event.EventCategory;

public enum EventType {
    USER_ADDED(EventCategory.USER),
    USER_UPDATED(EventCategory.USER),
    USER_DELETED(EventCategory.USER),
    USER_RENAMED(EventCategory.USER),
    USER_ACTIVATED(EventCategory.USER),
    USER_DEACTIVATED(EventCategory.USER),
    GROUP_ADDED(EventCategory.GROUP),
    GROUP_UPDATED(EventCategory.GROUP),
    GROUP_DELETED(EventCategory.GROUP),
    USER_MEMBERSHIP_ADDED(EventCategory.MEMBERSHIP),
    USER_MEMBERSHIP_DELETED(EventCategory.MEMBERSHIP),
    GROUP_MEMBERSHIP_ADDED(EventCategory.MEMBERSHIP),
    GROUP_MEMBERSHIP_DELETED(EventCategory.MEMBERSHIP),
    SPACE_ADDED(EventCategory.SPACE),
    SPACE_UPDATED(EventCategory.SPACE),
    SPACE_DELETED(EventCategory.SPACE),
    SPACE_ARCHIVED(EventCategory.SPACE),
    SPACE_UNARCHIVED(EventCategory.SPACE),
    SPACE_PERMISSION_ADDED(EventCategory.SPACE_PERMISSION),
    SPACE_PERMISSION_DELETED(EventCategory.SPACE_PERMISSION),
    GLOBAL_PERMISSION_ADDED(EventCategory.GLOBAL_PERMISSION),
    GLOBAL_PERMISSION_DELETED(EventCategory.GLOBAL_PERMISSION),
    USER_DIRECTORY_UPDATED(EventCategory.USER_DIRECTORY),
    APPLICATION_UPDATED(EventCategory.APPLICATION);

    private static final long serialVersionUID = -7841458171000632972L;
    private final EventCategory category;

    private EventType(EventCategory category) {
        this.category = category;
    }

    public EventCategory getCategory() {
        return this.category;
    }
}

