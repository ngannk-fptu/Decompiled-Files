/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.model.group.Group;

public class InvalidRoleException
extends InvalidGroupException {
    public InvalidRoleException(Group legacyRole, String message) {
        super(legacyRole, message);
    }
}

