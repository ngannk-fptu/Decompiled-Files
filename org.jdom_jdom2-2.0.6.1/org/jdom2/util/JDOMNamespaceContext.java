/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import org.jdom2.Namespace;
import org.jdom2.internal.ArrayCopy;

public final class JDOMNamespaceContext
implements NamespaceContext {
    private final Namespace[] namespacearray;

    public JDOMNamespaceContext(Namespace[] namespaces) {
        if (namespaces == null) {
            throw new IllegalArgumentException("Cannot process a null Namespace list");
        }
        this.namespacearray = ArrayCopy.copyOf(namespaces, namespaces.length);
        for (int i = 1; i < this.namespacearray.length; ++i) {
            Namespace n = this.namespacearray[i];
            if (n == null) {
                throw new IllegalArgumentException("Cannot process null namespace at position " + i);
            }
            String p = n.getPrefix();
            for (int j = 0; j < i; ++j) {
                if (!p.equals(this.namespacearray[j].getPrefix())) continue;
                throw new IllegalArgumentException("Cannot process multiple namespaces with the prefix '" + p + "'.");
            }
        }
    }

    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("NamespaceContext requires a non-null prefix");
        }
        if ("xml".equals(prefix)) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if ("xmlns".equals(prefix)) {
            return "http://www.w3.org/2000/xmlns/";
        }
        for (Namespace n : this.namespacearray) {
            if (!n.getPrefix().equals(prefix)) continue;
            return n.getURI();
        }
        return "";
    }

    public String getPrefix(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("NamespaceContext requires a non-null Namespace URI");
        }
        if ("http://www.w3.org/XML/1998/namespace".equals(namespaceURI)) {
            return "xml";
        }
        if ("http://www.w3.org/2000/xmlns/".equals(namespaceURI)) {
            return "xmlns";
        }
        for (Namespace n : this.namespacearray) {
            if (!n.getURI().equals(namespaceURI)) continue;
            return n.getPrefix();
        }
        return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("NamespaceContext requires a non-null Namespace URI");
        }
        if ("http://www.w3.org/XML/1998/namespace".equals(namespaceURI)) {
            return Collections.singleton("xml").iterator();
        }
        if ("http://www.w3.org/2000/xmlns/".equals(namespaceURI)) {
            return Collections.singleton("xmlns").iterator();
        }
        ArrayList<String> ret = new ArrayList<String>();
        for (Namespace n : this.namespacearray) {
            if (!n.getURI().equals(namespaceURI)) continue;
            ret.add(n.getPrefix());
        }
        return Collections.unmodifiableCollection(ret).iterator();
    }
}

