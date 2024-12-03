/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.namespace;

import javax.jcr.NamespaceException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.jackrabbit.spi.commons.namespace.NamespaceResolver;

public class SessionNamespaceResolver
implements NamespaceResolver {
    private final Session session;

    public SessionNamespaceResolver(Session session) {
        this.session = session;
    }

    @Override
    public String getPrefix(String uri) throws NamespaceException {
        try {
            return this.session.getNamespacePrefix(uri);
        }
        catch (RepositoryException e) {
            throw new NamespaceException("internal error: failed to resolve namespace uri", e);
        }
    }

    @Override
    public String getURI(String prefix) throws NamespaceException {
        try {
            return this.session.getNamespaceURI(prefix);
        }
        catch (RepositoryException e) {
            throw new NamespaceException("internal error: failed to resolve namespace prefix", e);
        }
    }
}

