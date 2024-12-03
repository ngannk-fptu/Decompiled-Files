/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.namespace;

import java.util.Collections;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public abstract class AbstractNamespaceContext
implements NamespaceContext {
    public final String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix can't be null");
        }
        if (prefix.equals("xml")) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if (prefix.equals("xmlns")) {
            return "http://www.w3.org/2000/xmlns/";
        }
        return this.doGetNamespaceURI(prefix);
    }

    protected abstract String doGetNamespaceURI(String var1);

    public final String getPrefix(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespaceURI can't be null");
        }
        if (namespaceURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (namespaceURI.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        return this.doGetPrefix(namespaceURI);
    }

    protected abstract String doGetPrefix(String var1);

    public final Iterator getPrefixes(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespaceURI can't be null");
        }
        if (namespaceURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return Collections.singleton("xml").iterator();
        }
        if (namespaceURI.equals("http://www.w3.org/2000/xmlns/")) {
            return Collections.singleton("xmlns").iterator();
        }
        return this.doGetPrefixes(namespaceURI);
    }

    protected abstract Iterator doGetPrefixes(String var1);
}

