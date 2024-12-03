/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.evt;

import com.ctc.wstx.util.BaseNsContext;
import com.ctc.wstx.util.DataUtil;
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

public class MergedNsContext
extends BaseNsContext {
    final NamespaceContext mParentCtxt;
    final List<Namespace> mNamespaces;
    Map<String, Namespace> mNsByPrefix = null;
    Map<String, Namespace> mNsByURI = null;

    protected MergedNsContext(NamespaceContext parentCtxt, List<Namespace> localNs) {
        this.mParentCtxt = parentCtxt;
        this.mNamespaces = localNs == null ? Collections.emptyList() : localNs;
    }

    public static BaseNsContext construct(NamespaceContext parentCtxt, List<Namespace> localNs) {
        return new MergedNsContext(parentCtxt, localNs);
    }

    @Override
    public String doGetNamespaceURI(String prefix) {
        Namespace ns;
        if (this.mNsByPrefix == null) {
            this.mNsByPrefix = this.buildByPrefixMap();
        }
        if ((ns = this.mNsByPrefix.get(prefix)) == null && this.mParentCtxt != null) {
            return this.mParentCtxt.getNamespaceURI(prefix);
        }
        return ns == null ? null : ns.getNamespaceURI();
    }

    @Override
    public String doGetPrefix(String nsURI) {
        Namespace ns;
        if (this.mNsByURI == null) {
            this.mNsByURI = this.buildByNsURIMap();
        }
        if ((ns = this.mNsByURI.get(nsURI)) == null && this.mParentCtxt != null) {
            return this.mParentCtxt.getPrefix(nsURI);
        }
        return ns == null ? null : ns.getPrefix();
    }

    @Override
    public Iterator<String> doGetPrefixes(String nsURI) {
        ArrayList<String> l = null;
        int len = this.mNamespaces.size();
        for (int i = 0; i < len; ++i) {
            String prefix;
            Namespace ns = this.mNamespaces.get(i);
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
        if (l == null) {
            return DataUtil.emptyIterator();
        }
        return l.iterator();
    }

    @Override
    public Iterator<Namespace> getNamespaces() {
        return this.mNamespaces.iterator();
    }

    @Override
    public void outputNamespaceDeclarations(Writer w) throws IOException {
        int len = this.mNamespaces.size();
        for (int i = 0; i < len; ++i) {
            Namespace ns = this.mNamespaces.get(i);
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

    @Override
    public void outputNamespaceDeclarations(XMLStreamWriter w) throws XMLStreamException {
        int len = this.mNamespaces.size();
        for (int i = 0; i < len; ++i) {
            Namespace ns = this.mNamespaces.get(i);
            if (ns.isDefaultNamespaceDeclaration()) {
                w.writeDefaultNamespace(ns.getNamespaceURI());
                continue;
            }
            w.writeNamespace(ns.getPrefix(), ns.getNamespaceURI());
        }
    }

    private Map<String, Namespace> buildByPrefixMap() {
        int len = this.mNamespaces.size();
        if (len == 0) {
            return Collections.emptyMap();
        }
        LinkedHashMap<String, Namespace> m = new LinkedHashMap<String, Namespace>(1 + len + (len >> 1));
        for (int i = 0; i < len; ++i) {
            Namespace ns = this.mNamespaces.get(i);
            String prefix = ns.getPrefix();
            if (prefix == null) {
                prefix = "";
            }
            m.put(prefix, ns);
        }
        return m;
    }

    private Map<String, Namespace> buildByNsURIMap() {
        int len = this.mNamespaces.size();
        if (len == 0) {
            return Collections.emptyMap();
        }
        LinkedHashMap<String, Namespace> m = new LinkedHashMap<String, Namespace>(1 + len + (len >> 1));
        for (int i = 0; i < len; ++i) {
            Namespace ns = this.mNamespaces.get(i);
            String uri = ns.getNamespaceURI();
            if (uri == null) {
                uri = "";
            }
            m.put(uri, ns);
        }
        return m;
    }
}

