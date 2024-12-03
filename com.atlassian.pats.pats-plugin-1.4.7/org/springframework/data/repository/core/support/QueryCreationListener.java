/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.repository.core.support;

import org.springframework.data.repository.query.RepositoryQuery;

public interface QueryCreationListener<T extends RepositoryQuery> {
    public void onCreation(T var1);
}

