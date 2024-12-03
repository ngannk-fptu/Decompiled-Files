/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.repository;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.naming.Context;
import javax.naming.NamingException;
import org.apache.jackrabbit.commons.repository.RepositoryFactory;

public class JNDIRepositoryFactory
implements RepositoryFactory {
    private final Context context;
    private final String name;

    public JNDIRepositoryFactory(Context context, String name) {
        this.context = context;
        this.name = name;
    }

    @Override
    public Repository getRepository() throws RepositoryException {
        try {
            Object repository = this.context.lookup(this.name);
            if (repository instanceof Repository) {
                return (Repository)repository;
            }
            if (repository == null) {
                throw new RepositoryException("Repository not found: The JNDI entry " + this.name + " is null");
            }
            throw new RepositoryException("Invalid repository: The JNDI entry " + this.name + " is an instance of " + repository.getClass().getName());
        }
        catch (NamingException e) {
            throw new RepositoryException("Repository not found: The JNDI entry " + this.name + " could not be looked up", e);
        }
    }
}

