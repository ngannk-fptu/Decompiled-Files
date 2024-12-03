/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.namespace;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.apache.axiom.util.namespace.AbstractNamespaceContext;

public class MapBasedNamespaceContext
extends AbstractNamespaceContext {
    private final Map namespaces;

    public MapBasedNamespaceContext(Map map) {
        this.namespaces = map;
    }

    protected String doGetNamespaceURI(String prefix) {
        String namespaceURI = (String)this.namespaces.get(prefix);
        return namespaceURI == null ? "" : namespaceURI;
    }

    protected String doGetPrefix(String nsURI) {
        for (Map.Entry entry : this.namespaces.entrySet()) {
            String uri = (String)entry.getValue();
            if (!uri.equals(nsURI)) continue;
            return (String)entry.getKey();
        }
        if (nsURI.length() == 0) {
            return "";
        }
        return null;
    }

    protected Iterator doGetPrefixes(String nsURI) {
        HashSet prefixes = null;
        for (Map.Entry entry : this.namespaces.entrySet()) {
            String uri = (String)entry.getValue();
            if (!uri.equals(nsURI)) continue;
            if (prefixes == null) {
                prefixes = new HashSet();
            }
            prefixes.add(entry.getKey());
        }
        if (prefixes != null) {
            return Collections.unmodifiableSet(prefixes).iterator();
        }
        if (nsURI.length() == 0) {
            return Collections.singleton("").iterator();
        }
        return Collections.EMPTY_LIST.iterator();
    }
}

