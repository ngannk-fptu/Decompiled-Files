/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

class NamespaceURICorrectingNamespaceContextWrapper
implements NamespaceContext {
    private final NamespaceContext parent;

    public NamespaceURICorrectingNamespaceContextWrapper(NamespaceContext parent) {
        this.parent = parent;
    }

    public String getNamespaceURI(String prefix) {
        String namespaceURI = this.parent.getNamespaceURI(prefix);
        return namespaceURI == null ? "" : namespaceURI;
    }

    public String getPrefix(String namespaceURI) {
        return this.parent.getPrefix(namespaceURI);
    }

    public Iterator getPrefixes(String namespaceURI) {
        return this.parent.getPrefixes(namespaceURI);
    }
}

