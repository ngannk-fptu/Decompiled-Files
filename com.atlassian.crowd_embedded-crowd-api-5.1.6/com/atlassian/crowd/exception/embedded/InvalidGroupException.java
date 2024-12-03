/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception.embedded;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.exception.CrowdException;

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

