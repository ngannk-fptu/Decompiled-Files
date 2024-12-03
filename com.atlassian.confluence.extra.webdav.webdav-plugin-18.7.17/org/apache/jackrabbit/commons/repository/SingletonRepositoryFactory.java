/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.repository;

import javax.jcr.Repository;
import org.apache.jackrabbit.commons.repository.RepositoryFactory;

public class SingletonRepositoryFactory
implements RepositoryFactory {
    private final Repository repository;

    public SingletonRepositoryFactory(Repository repository) {
        this.repository = repository;
    }

    @Override
    public Repository getRepository() {
        return this.repository;
    }
}

