/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.ObjectNotFoundException;

public class ApplicationNotFoundException
extends ObjectNotFoundException {
    private final String applicationName;
    private final Long id;

    public ApplicationNotFoundException(String applicationName) {
        this(applicationName, null);
    }

    public ApplicationNotFoundException(String applicationName, Throwable e) {
        super("Application <" + applicationName + "> does not exist", e);
        this.applicationName = applicationName;
        this.id = null;
    }

    public ApplicationNotFoundException(Long id) {
        this(id, null);
    }

    public ApplicationNotFoundException(Long id, Throwable e) {
        super("Application <" + id + "> does not exist", e);
        this.id = id;
        this.applicationName = null;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    public Long getId() {
        return this.id;
    }
}

