/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

class NamespaceURIInterningNamespaceContextWrapper
implements NamespaceContext {
    private final NamespaceContext parent;

    NamespaceURIInterningNamespaceContextWrapper(NamespaceContext parent) {
        this.parent = parent;
    }

    NamespaceContext getParent() {
        return this.parent;
    }

    private static String intern(String s) {
        return s == null ? null : s.intern();
    }

    public String getNamespaceURI(String prefix) {
        return NamespaceURIInterningNamespaceContextWrapper.intern(this.parent.getNamespaceURI(prefix));
    }

    public String getPrefix(String namespaceURI) {
        return this.parent.getPrefix(namespaceURI);
    }

    public Iterator getPrefixes(String namespaceURI) {
        return this.parent.getPrefixes(namespaceURI);
    }
}

