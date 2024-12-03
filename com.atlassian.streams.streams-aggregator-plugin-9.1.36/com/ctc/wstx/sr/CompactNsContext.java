/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sr;

import com.ctc.wstx.util.BaseNsContext;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.stax2.ri.EmptyIterator;
import org.codehaus.stax2.ri.SingletonIterator;
import org.codehaus.stax2.ri.evt.NamespaceEventImpl;

public final class CompactNsContext
extends BaseNsContext {
    final Location mLocation;
    final String[] mNamespaces;
    final int mNsLength;
    final int mFirstLocalNs;
    transient ArrayList mNsList;

    public CompactNsContext(Location loc, String defaultNsURI, String[] namespaces, int nsLen, int firstLocal) {
        this.mLocation = loc;
        this.mNamespaces = namespaces;
        this.mNsLength = nsLen;
        this.mFirstLocalNs = firstLocal;
    }

    public String doGetNamespaceURI(String prefix) {
        String[] ns = this.mNamespaces;
        if (prefix.length() == 0) {
            for (int i = this.mNsLength - 2; i >= 0; i -= 2) {
                if (ns[i] != null) continue;
                return ns[i + 1];
            }
            return null;
        }
        for (int i = this.mNsLength - 2; i >= 0; i -= 2) {
            if (!prefix.equals(ns[i])) continue;
            return ns[i + 1];
        }
        return null;
    }

    public String doGetPrefix(String nsURI) {
        String[] ns = this.mNamespaces;
        int len = this.mNsLength;
        block0: for (int i = len - 1; i > 0; i -= 2) {
            if (!nsURI.equals(ns[i])) continue;
            String prefix = ns[i - 1];
            for (int j = i + 1; j < len; j += 2) {
                if (ns[j] == prefix) continue block0;
            }
            String uri = ns[i - 1];
            return uri == null ? "" : uri;
        }
        return null;
    }

    public Iterator doGetPrefixes(String nsURI) {
        String[] ns = this.mNamespaces;
        int len = this.mNsLength;
        String first = null;
        ArrayList<String> all = null;
        block0: for (int i = len - 1; i > 0; i -= 2) {
            String currNS = ns[i];
            if (currNS != nsURI && !currNS.equals(nsURI)) continue;
            String prefix = ns[i - 1];
            for (int j = i + 1; j < len; j += 2) {
                if (ns[j] == prefix) continue block0;
            }
            if (prefix == null) {
                prefix = "";
            }
            if (first == null) {
                first = prefix;
                continue;
            }
            if (all == null) {
                all = new ArrayList<String>();
                all.add(first);
            }
            all.add(prefix);
        }
        if (all != null) {
            return all.iterator();
        }
        if (first != null) {
            return new SingletonIterator(first);
        }
        return EmptyIterator.getInstance();
    }

    public Iterator getNamespaces() {
        if (this.mNsList == null) {
            int firstLocal = this.mFirstLocalNs;
            int len = this.mNsLength - firstLocal;
            if (len == 0) {
                return EmptyIterator.getInstance();
            }
            if (len == 2) {
                return new SingletonIterator(NamespaceEventImpl.constructNamespace(this.mLocation, this.mNamespaces[firstLocal], this.mNamespaces[firstLocal + 1]));
            }
            ArrayList<NamespaceEventImpl> l = new ArrayList<NamespaceEventImpl>(len >> 1);
            String[] ns = this.mNamespaces;
            len = this.mNsLength;
            while (firstLocal < len) {
                l.add(NamespaceEventImpl.constructNamespace(this.mLocation, ns[firstLocal], ns[firstLocal + 1]));
                firstLocal += 2;
            }
            this.mNsList = l;
        }
        return this.mNsList.iterator();
    }

    public void outputNamespaceDeclarations(Writer w) throws IOException {
        String[] ns = this.mNamespaces;
        int len = this.mNsLength;
        for (int i = this.mFirstLocalNs; i < len; i += 2) {
            w.write(32);
            w.write("xmlns");
            String prefix = ns[i];
            if (prefix != null && prefix.length() > 0) {
                w.write(58);
                w.write(prefix);
            }
            w.write("=\"");
            w.write(ns[i + 1]);
            w.write(34);
        }
    }

    public void outputNamespaceDeclarations(XMLStreamWriter w) throws XMLStreamException {
        String[] ns = this.mNamespaces;
        int len = this.mNsLength;
        for (int i = this.mFirstLocalNs; i < len; i += 2) {
            String nsURI = ns[i + 1];
            String prefix = ns[i];
            if (prefix != null && prefix.length() > 0) {
                w.writeNamespace(prefix, nsURI);
                continue;
            }
            w.writeDefaultNamespace(nsURI);
        }
    }
}

