/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.CrowdException;

public class InvalidMembershipException
extends CrowdException {
    public InvalidMembershipException(String message) {
        super(message);
    }

    public InvalidMembershipException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMembershipException(Throwable cause) {
        super(cause);
    }
}

