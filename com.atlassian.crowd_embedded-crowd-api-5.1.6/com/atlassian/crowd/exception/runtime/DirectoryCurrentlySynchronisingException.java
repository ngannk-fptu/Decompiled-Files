/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception.runtime;

import com.atlassian.crowd.exception.runtime.CrowdRuntimeException;

public class DirectoryCurrentlySynchronisingException
extends CrowdRuntimeException {
    final long directoryId;

    public DirectoryCurrentlySynchronisingException(long directoryId) {
        this(directoryId, null);
    }

    public DirectoryCurrentlySynchronisingException(long directoryId, Throwable cause) {
        super("Directory " + directoryId + " is currently synchronising.", cause);
        this.directoryId = directoryId;
    }

    public long getDirectoryId() {
        return this.directoryId;
    }
}

