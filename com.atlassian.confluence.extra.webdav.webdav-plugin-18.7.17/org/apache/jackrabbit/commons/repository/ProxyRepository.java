/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.repository;

import java.util.HashMap;
import java.util.Map;
import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import org.apache.jackrabbit.commons.AbstractRepository;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.commons.repository.RepositoryFactory;

public class ProxyRepository
extends AbstractRepository {
    private final RepositoryFactory factory;
    private final Map<String, String> parameters = new HashMap<String, String>();

    public ProxyRepository(RepositoryFactory factory) {
        this.factory = factory;
    }

    public ProxyRepository(Map<String, String> parameters) {
        this.factory = null;
        this.parameters.putAll(parameters);
    }

    public ProxyRepository(String uri) {
        this.factory = null;
        this.parameters.put("org.apache.jackrabbit.repository.uri", uri);
    }

    protected ProxyRepository() {
        this.factory = null;
    }

    protected Repository getRepository() throws RepositoryException {
        if (this.factory != null) {
            return this.factory.getRepository();
        }
        return JcrUtils.getRepository(this.parameters);
    }

    @Override
    public String[] getDescriptorKeys() {
        try {
            return this.getRepository().getDescriptorKeys();
        }
        catch (RepositoryException e) {
            return new String[0];
        }
    }

    @Override
    public boolean isSingleValueDescriptor(String key) {
        try {
            return this.getRepository().isSingleValueDescriptor(key);
        }
        catch (RepositoryException e) {
            return false;
        }
    }

    @Override
    public String getDescriptor(String key) {
        try {
            return this.getRepository().getDescriptor(key);
        }
        catch (RepositoryException e) {
            return null;
        }
    }

    @Override
    public Value getDescriptorValue(String key) {
        try {
            return this.getRepository().getDescriptorValue(key);
        }
        catch (RepositoryException e) {
            return null;
        }
    }

    @Override
    public Value[] getDescriptorValues(String key) {
        try {
            return this.getRepository().getDescriptorValues(key);
        }
        catch (RepositoryException e) {
            return null;
        }
    }

    @Override
    public Session login(Credentials credentials, String workspace) throws RepositoryException {
        return this.getRepository().login(credentials, workspace);
    }
}

