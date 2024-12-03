/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.repository;

import javax.jcr.Credentials;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Session;
import javax.jcr.Value;
import org.apache.jackrabbit.commons.AbstractRepository;

public class EmptyRepository
extends AbstractRepository {
    @Override
    public String getDescriptor(String key) {
        return null;
    }

    @Override
    public Value getDescriptorValue(String key) {
        return null;
    }

    @Override
    public Value[] getDescriptorValues(String key) {
        return null;
    }

    @Override
    public boolean isSingleValueDescriptor(String key) {
        return false;
    }

    @Override
    public String[] getDescriptorKeys() {
        return new String[0];
    }

    @Override
    public Session login(Credentials credentials, String workspace) throws NoSuchWorkspaceException {
        throw new NoSuchWorkspaceException("Empty repository");
    }
}

