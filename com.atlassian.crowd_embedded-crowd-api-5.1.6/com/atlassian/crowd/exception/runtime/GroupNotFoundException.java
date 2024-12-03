/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception.runtime;

import com.atlassian.crowd.exception.runtime.CrowdRuntimeException;

public class GroupNotFoundException
extends CrowdRuntimeException {
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

