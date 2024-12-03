/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom.factory;

import java.util.HashSet;
import java.util.Iterator;
import org.apache.axiom.om.impl.llom.factory.DOMUtils;
import org.apache.axiom.om.impl.llom.factory.DOMXMLStreamReader;
import org.apache.axiom.util.namespace.AbstractNamespaceContext;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class DOMNamespaceContext
extends AbstractNamespaceContext {
    private final DOMXMLStreamReader reader;

    DOMNamespaceContext(DOMXMLStreamReader reader) {
        this.reader = reader;
    }

    protected String doGetNamespaceURI(String prefix) {
        String namespaceURI = this.reader.getNamespaceURI(prefix);
        return namespaceURI == null ? "" : namespaceURI;
    }

    protected String doGetPrefix(String namespaceURI) {
        HashSet<String> seenPrefixes = new HashSet<String>();
        Node current = this.reader.currentNode();
        do {
            NamedNodeMap attributes;
            if ((attributes = current.getAttributes()) == null) continue;
            int l = attributes.getLength();
            for (int i = 0; i < l; ++i) {
                Attr attr = (Attr)attributes.item(i);
                if (!DOMUtils.isNSDecl(attr)) continue;
                String prefix = DOMUtils.getNSDeclPrefix(attr);
                if (prefix == null) {
                    prefix = "";
                }
                if (!seenPrefixes.add(prefix) || !attr.getValue().equals(namespaceURI)) continue;
                return prefix;
            }
        } while ((current = current.getParentNode()) != null);
        return null;
    }

    protected Iterator doGetPrefixes(String namespaceURI) {
        HashSet<String> seenPrefixes = new HashSet<String>();
        HashSet<String> matchingPrefixes = new HashSet<String>();
        Node current = this.reader.currentNode();
        do {
            NamedNodeMap attributes;
            if ((attributes = current.getAttributes()) == null) continue;
            int l = attributes.getLength();
            for (int i = 0; i < l; ++i) {
                Attr attr = (Attr)attributes.item(i);
                if (!DOMUtils.isNSDecl(attr)) continue;
                String prefix = DOMUtils.getNSDeclPrefix(attr);
                if (prefix == null) {
                    prefix = "";
                }
                if (!seenPrefixes.add(prefix) || !attr.getValue().equals(namespaceURI)) continue;
                matchingPrefixes.add(prefix);
            }
        } while ((current = current.getParentNode()) != null);
        return matchingPrefixes.iterator();
    }
}

