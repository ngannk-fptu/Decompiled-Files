/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.util.namespace.AbstractNamespaceContext;

class LiveNamespaceContext
extends AbstractNamespaceContext {
    private final OMElement element;

    public LiveNamespaceContext(OMElement element) {
        this.element = element;
    }

    protected String doGetNamespaceURI(String prefix) {
        OMNamespace ns = this.element.findNamespaceURI(prefix);
        return ns == null ? "" : ns.getNamespaceURI();
    }

    protected String doGetPrefix(String namespaceURI) {
        OMNamespace ns = this.element.findNamespace(namespaceURI, null);
        return ns == null ? null : ns.getPrefix();
    }

    protected Iterator doGetPrefixes(String namespaceURI) {
        ArrayList<String> prefixes = new ArrayList<String>();
        Iterator it = this.element.getNamespacesInScope();
        while (it.hasNext()) {
            OMNamespace ns = (OMNamespace)it.next();
            if (!ns.getNamespaceURI().equals(namespaceURI)) continue;
            prefixes.add(ns.getPrefix());
        }
        return prefixes.iterator();
    }
}

