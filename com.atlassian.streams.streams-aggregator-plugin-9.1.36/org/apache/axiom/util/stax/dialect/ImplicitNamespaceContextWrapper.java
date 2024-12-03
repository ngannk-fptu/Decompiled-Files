/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import org.apache.axiom.util.namespace.AbstractNamespaceContext;

class ImplicitNamespaceContextWrapper
extends AbstractNamespaceContext {
    private final NamespaceContext parent;

    public ImplicitNamespaceContextWrapper(NamespaceContext parent) {
        this.parent = parent;
    }

    protected String doGetNamespaceURI(String prefix) {
        return this.parent.getNamespaceURI(prefix);
    }

    protected String doGetPrefix(String namespaceURI) {
        return this.parent.getPrefix(namespaceURI);
    }

    protected Iterator doGetPrefixes(String namespaceURI) {
        return this.parent.getPrefixes(namespaceURI);
    }
}

