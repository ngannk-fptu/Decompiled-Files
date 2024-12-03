/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.CrowdException
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.CrowdException;
import com.atlassian.crowd.model.group.Group;

public class InvalidGroupException
extends CrowdException {
    private final Group group;

    public InvalidGroupException(Group group, Throwable cause) {
        super(cause);
        this.group = group;
    }

    public InvalidGroupException(Group group, String message) {
        super(message);
        this.group = group;
    }

    public InvalidGroupException(Group group, String message, Throwable cause) {
        super(message, cause);
        this.group = group;
    }

    public Group getGroup() {
        return this.group;
    }
}

