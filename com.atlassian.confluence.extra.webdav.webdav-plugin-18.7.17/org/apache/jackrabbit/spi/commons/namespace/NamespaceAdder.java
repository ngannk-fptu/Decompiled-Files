/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.namespace;

import java.util.Map;
import javax.jcr.NamespaceException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import org.apache.jackrabbit.spi.commons.namespace.NamespaceMapping;

public class NamespaceAdder {
    private final NamespaceRegistry registry;

    public NamespaceAdder(NamespaceRegistry nsr) {
        this.registry = nsr;
    }

    public void addNamespaces(NamespaceMapping nsm) throws NamespaceException, UnsupportedRepositoryOperationException, RepositoryException {
        Map<String, String> m = nsm.getPrefixToURIMapping();
        for (Map.Entry<String, String> e : m.entrySet()) {
            String prefix = e.getKey();
            String uri = e.getValue();
            this.registry.registerNamespace(prefix, uri);
        }
    }

    public void addNamespace(String prefix, String uri) throws NamespaceException, UnsupportedRepositoryOperationException, RepositoryException {
        this.registry.registerNamespace(prefix, uri);
    }
}

