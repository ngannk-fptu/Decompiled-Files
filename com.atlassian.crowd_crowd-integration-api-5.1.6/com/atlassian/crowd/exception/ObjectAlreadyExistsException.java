/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.CrowdException
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.CrowdException;

public class ObjectAlreadyExistsException
extends CrowdException {
    public ObjectAlreadyExistsException(String s) {
        super(s);
    }

    public ObjectAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

