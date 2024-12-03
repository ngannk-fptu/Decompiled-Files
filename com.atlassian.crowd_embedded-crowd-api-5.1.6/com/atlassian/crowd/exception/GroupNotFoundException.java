/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.ObjectNotFoundException;

public class GroupNotFoundException
extends ObjectNotFoundException {
    private final String groupName;

    public GroupNotFoundException(String groupName) {
        this(groupName, null);
    }

    public GroupNotFoundException(String groupName, Throwable e) {
        super("Group <" + groupName + "> does not exist", e);
        this.groupName = groupName;
    }

    public String getGroupName() {
        return this.groupName;
    }
}

