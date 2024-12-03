/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.CrowdException;

public class ReadOnlyGroupException
extends CrowdException {
    private final String groupName;

    public ReadOnlyGroupException(String groupName) {
        this(groupName, null);
    }

    public ReadOnlyGroupException(String groupName, Throwable e) {
        super("Group <" + groupName + "> is read-only and cannot be updated", e);
        this.groupName = groupName;
    }

    public String getGroupName() {
        return this.groupName;
    }
}

