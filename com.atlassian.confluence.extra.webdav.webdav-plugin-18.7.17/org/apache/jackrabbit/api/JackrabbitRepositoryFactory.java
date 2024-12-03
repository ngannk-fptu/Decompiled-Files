/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.api;

import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.api.management.RepositoryManager;

public interface JackrabbitRepositoryFactory
extends RepositoryFactory {
    public RepositoryManager getRepositoryManager(JackrabbitRepository var1) throws RepositoryException;
}

