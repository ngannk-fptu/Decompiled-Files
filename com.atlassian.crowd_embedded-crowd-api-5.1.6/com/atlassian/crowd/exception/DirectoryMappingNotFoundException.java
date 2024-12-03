/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.ObjectNotFoundException;

public class DirectoryMappingNotFoundException
extends ObjectNotFoundException {
    private final long applicationId;
    private final long directoryId;

    public DirectoryMappingNotFoundException(long applicationId, long directoryId) {
        super(String.format("The directory mapping between application <%d> and directory <%d> does not exist", applicationId, directoryId));
        this.applicationId = applicationId;
        this.directoryId = directoryId;
    }

    public long getApplicationId() {
        return this.applicationId;
    }

    public long getDirectoryId() {
        return this.directoryId;
    }
}

