/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.evt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.events.Namespace;

public class MergedNsContext
implements NamespaceContext {
    final NamespaceContext _parentCtxt;
    final List<Namespace> _namespaces;

    protected MergedNsContext(NamespaceContext parentCtxt, List<Namespace> localNs) {
        this._parentCtxt = parentCtxt;
        this._namespaces = localNs == null ? Collections.emptyList() : localNs;
    }

    public static MergedNsContext construct(NamespaceContext parentCtxt, List<Namespace> localNs) {
        return new MergedNsContext(parentCtxt, localNs);
    }

    @Override
    public String getNamespaceURI(String prefix) {
        String uri;
        if (prefix == null) {
            throw new IllegalArgumentException("Illegal to pass null prefix");
        }
        int len = this._namespaces.size();
        for (int i = 0; i < len; ++i) {
            Namespace ns = this._namespaces.get(i);
            if (!prefix.equals(ns.getPrefix())) continue;
            return ns.getNamespaceURI();
        }
        if (this._parentCtxt != null && (uri = this._parentCtxt.getNamespaceURI(prefix)) != null) {
            return uri;
        }
        if (prefix.equals("xml")) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if (prefix.equals("xmlns")) {
            return "http://www.w3.org/2000/xmlns/";
        }
        return null;
    }

    @Override
    public String getPrefix(String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        int len = this._namespaces.size();
        for (int i = 0; i < len; ++i) {
            Namespace ns = this._namespaces.get(i);
            if (!nsURI.equals(ns.getNamespaceURI())) continue;
            return ns.getPrefix();
        }
        if (this._parentCtxt != null) {
            String uri2;
            String prefix = this._parentCtxt.getPrefix(nsURI);
            if (prefix != null && (uri2 = this.getNamespaceURI(prefix)).equals(nsURI)) {
                return prefix;
            }
            Iterator<String> it = this._parentCtxt.getPrefixes(nsURI);
            while (it.hasNext()) {
                String uri22;
                String p2 = it.next();
                if (p2.equals(prefix) || !(uri22 = this.getNamespaceURI(p2)).equals(nsURI)) continue;
                return p2;
            }
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        return null;
    }

    @Override
    public Iterator<String> getPrefixes(String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        ArrayList<String> l = null;
        int len = this._namespaces.size();
        for (int i = 0; i < len; ++i) {
            Namespace ns = this._namespaces.get(i);
            if (!nsURI.equals(ns.getNamespaceURI())) continue;
            l = this.addToList(l, ns.getPrefix());
        }
        if (this._parentCtxt != null) {
            Iterator<String> it = this._parentCtxt.getPrefixes(nsURI);
            while (it.hasNext()) {
                String p2 = it.next();
                String uri2 = this.getNamespaceURI(p2);
                if (!uri2.equals(nsURI)) continue;
                l = this.addToList(l, p2);
            }
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            l = this.addToList(l, "xml");
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            l = this.addToList(l, "xmlns");
        }
        return null;
    }

    protected <T> ArrayList<T> addToList(ArrayList<T> l, T value) {
        if (l == null) {
            l = new ArrayList();
        }
        l.add(value);
        return l;
    }
}

