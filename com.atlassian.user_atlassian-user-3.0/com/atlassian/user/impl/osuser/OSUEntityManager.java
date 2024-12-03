/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.osuser;

import com.atlassian.user.EntityManager;
import com.atlassian.user.repository.RepositoryIdentifier;

public abstract class OSUEntityManager
implements EntityManager {
    protected RepositoryIdentifier repository;

    public OSUEntityManager(RepositoryIdentifier repo) {
        this.repository = repo;
    }

    public RepositoryIdentifier getConfiguration() {
        return this.repository;
    }

    public boolean isCreative() {
        return true;
    }

    public RepositoryIdentifier getIdentifier() {
        return this.repository;
    }
}

