/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.CrowdException;

public class DirectoryNotFoundException
extends CrowdException {
    private final String directoryName;
    private final Long id;

    public DirectoryNotFoundException(String directoryName) {
        this(directoryName, null);
    }

    public DirectoryNotFoundException(String directoryName, Throwable e) {
        super("Directory <" + directoryName + "> does not exist", e);
        this.directoryName = directoryName;
        this.id = null;
    }

    public DirectoryNotFoundException(Long id) {
        this(id, null);
    }

    public DirectoryNotFoundException(Long id, Throwable e) {
        super("Directory <" + id + "> does not exist", e);
        this.id = id;
        this.directoryName = null;
    }

    public DirectoryNotFoundException(Throwable e) {
        super(e);
        this.id = null;
        this.directoryName = null;
    }

    public String getDirectoryName() {
        return this.directoryName;
    }

    public Long getId() {
        return this.id;
    }
}

