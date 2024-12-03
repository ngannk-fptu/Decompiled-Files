/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.CrowdException
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.CrowdException;

public class NestedGroupsNotSupportedException
extends CrowdException {
    public NestedGroupsNotSupportedException(long directoryId) {
        super("Directory with id <" + directoryId + "> does not support nested groups");
    }
}

