/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 */
package org.springframework.data.repository.core;

import org.springframework.dao.InvalidDataAccessApiUsageException;

public class RepositoryCreationException
extends InvalidDataAccessApiUsageException {
    private final Class<?> repositoryInterface;

    public RepositoryCreationException(String msg, Class<?> repositoryInterface) {
        super(msg);
        this.repositoryInterface = repositoryInterface;
    }

    public RepositoryCreationException(String msg, Throwable cause, Class<?> repositoryInterface) {
        super(msg, cause);
        this.repositoryInterface = repositoryInterface;
    }

    public Class<?> getRepositoryInterface() {
        return this.repositoryInterface;
    }
}

