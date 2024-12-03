/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.OperationFailedException
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.exception.OperationFailedException;

public interface FastEntityCountProvider {
    public long getUserCount() throws OperationFailedException;

    public long getGroupCount() throws OperationFailedException;
}

