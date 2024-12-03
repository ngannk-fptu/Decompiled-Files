/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.repository;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;

public interface RepositoryFactory {
    public Repository getRepository() throws RepositoryException;
}

