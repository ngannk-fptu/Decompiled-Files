/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.xml;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Stack;
import org.bedework.util.xml.XmlEmit;

class XmlEmitNamespaces {
    private boolean mustEmitNS;
    private int scopeLevel;
    private Stack<XmlEmit.NameSpace> namespaces = new Stack();
    private HashMap<String, XmlEmit.NameSpace> nsMap = new HashMap();
    private int nsIndex;
    private String defaultNs;

    public void addNs(XmlEmit.NameSpace val, boolean makeDefaultNs) throws IOException {
        if (val.abbrev == null) {
            val.abbrev = "ns" + this.nsIndex;
            ++this.nsIndex;
        }
        val.level = this.scopeLevel;
        val.defaultNs = makeDefaultNs;
        for (XmlEmit.NameSpace ns : this.nsMap.values()) {
            if (val.equals(ns) || val.level != ns.level || !val.abbrev.equals(ns.abbrev)) continue;
            throw new IOException("Duplicate namespace alias for " + val.ns);
        }
        this.nsMap.put(val.ns, val);
        this.mustEmitNS = true;
        this.namespaces.push(val);
        if (makeDefaultNs) {
            this.defaultNs = val.ns;
        }
    }

    public void startScope() {
        ++this.scopeLevel;
    }

    public void endScope() {
        while (!this.namespaces.empty()) {
            XmlEmit.NameSpace ns = this.namespaces.peek();
            if (ns.level < this.scopeLevel) break;
            this.namespaces.pop();
        }
        this.nsMap.clear();
        for (int i = 0; i < this.namespaces.size(); ++i) {
            XmlEmit.NameSpace ns = (XmlEmit.NameSpace)this.namespaces.elementAt(i);
            this.nsMap.put(ns.ns, ns);
        }
        --this.scopeLevel;
    }

    public XmlEmit.NameSpace getNameSpace(String ns) {
        return this.nsMap.get(ns);
    }

    public String getNsAbbrev(String ns) {
        XmlEmit.NameSpace n = this.nsMap.get(ns);
        if (n == null) {
            return null;
        }
        return n.abbrev;
    }

    public void emitNsAbbr(String ns, Writer wtr) throws IOException {
        if (ns == null || ns.equals(this.defaultNs)) {
            return;
        }
        String abbr = this.getNsAbbrev(ns);
        if (abbr != null) {
            wtr.write(abbr);
            wtr.write(":");
        }
    }

    public void emitNs(Writer wtr) throws IOException {
        if (!this.mustEmitNS) {
            return;
        }
        String delim = "";
        for (String nsp : this.nsMap.keySet()) {
            wtr.write(delim);
            delim = "\n             ";
            wtr.write(" xmlns");
            String abbr = this.getNsAbbrev(nsp);
            if (abbr != null && !nsp.equals(this.defaultNs)) {
                wtr.write(":");
                wtr.write(abbr);
            }
            wtr.write("=\"");
            wtr.write(nsp);
            wtr.write("\"");
        }
        this.mustEmitNS = false;
    }
}

