/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.evt;

import com.ctc.wstx.util.BaseNsContext;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Namespace;
import org.codehaus.stax2.ri.EmptyIterator;

public class MergedNsContext
extends BaseNsContext {
    final NamespaceContext mParentCtxt;
    final List mNamespaces;
    Map mNsByPrefix = null;
    Map mNsByURI = null;

    protected MergedNsContext(NamespaceContext parentCtxt, List localNs) {
        this.mParentCtxt = parentCtxt;
        this.mNamespaces = localNs == null ? Collections.EMPTY_LIST : localNs;
    }

    public static BaseNsContext construct(NamespaceContext parentCtxt, List localNs) {
        return new MergedNsContext(parentCtxt, localNs);
    }

    public String doGetNamespaceURI(String prefix) {
        Namespace ns;
        if (this.mNsByPrefix == null) {
            this.mNsByPrefix = this.buildByPrefixMap();
        }
        if ((ns = (Namespace)this.mNsByPrefix.get(prefix)) == null && this.mParentCtxt != null) {
            return this.mParentCtxt.getNamespaceURI(prefix);
        }
        return ns == null ? null : ns.getNamespaceURI();
    }

    public String doGetPrefix(String nsURI) {
        Namespace ns;
        if (this.mNsByURI == null) {
            this.mNsByURI = this.buildByNsURIMap();
        }
        if ((ns = (Namespace)this.mNsByURI.get(nsURI)) == null && this.mParentCtxt != null) {
            return this.mParentCtxt.getPrefix(nsURI);
        }
        return ns == null ? null : ns.getPrefix();
    }

    public Iterator doGetPrefixes(String nsURI) {
        ArrayList<String> l = null;
        int len = this.mNamespaces.size();
        for (int i = 0; i < len; ++i) {
            String prefix;
            Namespace ns = (Namespace)this.mNamespaces.get(i);
            String uri = ns.getNamespaceURI();
            if (uri == null) {
                uri = "";
            }
            if (!uri.equals(nsURI)) continue;
            if (l == null) {
                l = new ArrayList<String>();
            }
            l.add((prefix = ns.getPrefix()) == null ? "" : prefix);
        }
        if (this.mParentCtxt != null) {
            Iterator<String> it = this.mParentCtxt.getPrefixes(nsURI);
            if (l == null) {
                return it;
            }
            while (it.hasNext()) {
                l.add(it.next());
            }
        }
        return l == null ? EmptyIterator.getInstance() : l.iterator();
    }

    public Iterator getNamespaces() {
        return this.mNamespaces.iterator();
    }

    public void outputNamespaceDeclarations(Writer w) throws IOException {
        int len = this.mNamespaces.size();
        for (int i = 0; i < len; ++i) {
            Namespace ns = (Namespace)this.mNamespaces.get(i);
            w.write(32);
            w.write("xmlns");
            if (!ns.isDefaultNamespaceDeclaration()) {
                w.write(58);
                w.write(ns.getPrefix());
            }
            w.write("=\"");
            w.write(ns.getNamespaceURI());
            w.write(34);
        }
    }

    public void outputNamespaceDeclarations(XMLStreamWriter w) throws XMLStreamException {
        int len = this.mNamespaces.size();
        for (int i = 0; i < len; ++i) {
            Namespace ns = (Namespace)this.mNamespaces.get(i);
            if (ns.isDefaultNamespaceDeclaration()) {
                w.writeDefaultNamespace(ns.getNamespaceURI());
                continue;
            }
            w.writeNamespace(ns.getPrefix(), ns.getNamespaceURI());
        }
    }

    private Map buildByPrefixMap() {
        int len = this.mNamespaces.size();
        if (len == 0) {
            return Collections.EMPTY_MAP;
        }
        LinkedHashMap<String, Namespace> m = new LinkedHashMap<String, Namespace>(1 + len + (len >> 1));
        for (int i = 0; i < len; ++i) {
            Namespace ns = (Namespace)this.mNamespaces.get(i);
            String prefix = ns.getPrefix();
            if (prefix == null) {
                prefix = "";
            }
            m.put(prefix, ns);
        }
        return m;
    }

    private Map buildByNsURIMap() {
        int len = this.mNamespaces.size();
        if (len == 0) {
            return Collections.EMPTY_MAP;
        }
        LinkedHashMap<String, Namespace> m = new LinkedHashMap<String, Namespace>(1 + len + (len >> 1));
        for (int i = 0; i < len; ++i) {
            Namespace ns = (Namespace)this.mNamespaces.get(i);
            String uri = ns.getNamespaceURI();
            if (uri == null) {
                uri = "";
            }
            m.put(uri, ns);
        }
        return m;
    }
}

