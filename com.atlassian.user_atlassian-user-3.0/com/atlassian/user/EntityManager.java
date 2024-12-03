/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user;

import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.repository.RepositoryIdentifier;

public interface EntityManager {
    public RepositoryIdentifier getIdentifier();

    public RepositoryIdentifier getRepository(Entity var1) throws EntityException;

    public boolean isCreative();
}

