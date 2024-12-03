/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.utils;

import java.util.Iterator;
import java.util.Objects;
import javax.xml.namespace.NamespaceContext;
import org.w3c.dom.Node;

public class DOMNamespaceContext
implements NamespaceContext {
    private Node context;

    public DOMNamespaceContext(Node context) {
        this.setContext(context);
    }

    public void setContext(Node context) {
        this.context = context;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        String namespaceURI;
        if (prefix == null) {
            throw new IllegalArgumentException("prefix is null");
        }
        if (prefix.equals("")) {
            prefix = null;
        }
        if (this.context != null && (namespaceURI = this.context.lookupNamespaceURI(prefix)) != null) {
            return namespaceURI;
        }
        if (prefix == null) {
            return "";
        }
        if (prefix.equals("xml")) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if (prefix.equals("xmlns")) {
            return "http://www.w3.org/2000/xmlns/";
        }
        return "";
    }

    @Override
    public String getPrefix(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespace URI is null");
        }
        if (namespaceURI.equals("")) {
            namespaceURI = null;
        }
        if (this.context != null) {
            String prefix = this.context.lookupPrefix(namespaceURI);
            if (prefix != null) {
                return prefix;
            }
            if (Objects.equals(this.context.lookupNamespaceURI(null), namespaceURI)) {
                return "";
            }
        }
        if (namespaceURI == null && this.context != null) {
            return this.context.lookupNamespaceURI(null) != null ? null : "";
        }
        if ("http://www.w3.org/XML/1998/namespace".equals(namespaceURI)) {
            return "xml";
        }
        if ("http://www.w3.org/2000/xmlns/".equals(namespaceURI)) {
            return "xmlns";
        }
        return null;
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        throw new UnsupportedOperationException();
    }
}

