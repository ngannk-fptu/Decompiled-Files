/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.namespace;

import javax.jcr.NamespaceException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.commons.namespace.NamespaceResolver;

public class RegistryNamespaceResolver
implements NamespaceResolver {
    private final NamespaceRegistry registry;

    public RegistryNamespaceResolver(NamespaceRegistry registry) {
        this.registry = registry;
    }

    @Override
    public String getPrefix(String uri) throws NamespaceException {
        try {
            return this.registry.getPrefix(uri);
        }
        catch (RepositoryException e2) {
            NamespaceException e2;
            if (!(e2 instanceof NamespaceException)) {
                e2 = new NamespaceException("Failed to resolve namespace URI: " + uri, e2);
            }
            throw (NamespaceException)e2;
        }
    }

    @Override
    public String getURI(String prefix) throws NamespaceException {
        try {
            return this.registry.getURI(prefix);
        }
        catch (RepositoryException e2) {
            NamespaceException e2;
            if (!(e2 instanceof NamespaceException)) {
                e2 = new NamespaceException("Failed to resolve namespace prefix: " + prefix, e2);
            }
            throw (NamespaceException)e2;
        }
    }
}

