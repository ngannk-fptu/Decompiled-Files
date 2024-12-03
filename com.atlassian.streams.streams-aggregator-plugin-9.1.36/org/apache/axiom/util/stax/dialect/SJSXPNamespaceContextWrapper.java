/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

class SJSXPNamespaceContextWrapper
implements NamespaceContext {
    private final NamespaceContext parent;

    public SJSXPNamespaceContextWrapper(NamespaceContext parent) {
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
        ArrayList<String> prefixes = new ArrayList<String>(5);
        Iterator<String> it = this.parent.getPrefixes(namespaceURI);
        while (it.hasNext()) {
            String prefix = it.next();
            String actualNamespaceURI = this.parent.getNamespaceURI(prefix);
            if (namespaceURI != actualNamespaceURI && (namespaceURI == null || !namespaceURI.equals(actualNamespaceURI))) continue;
            prefixes.add(prefix);
        }
        return prefixes.iterator();
    }
}

