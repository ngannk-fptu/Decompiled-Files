/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.stax2.ri.evt.NamespaceEventImpl
 */
package com.ctc.wstx.sr;

import com.ctc.wstx.util.BaseNsContext;
import com.ctc.wstx.util.DataUtil;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Namespace;
import org.codehaus.stax2.ri.evt.NamespaceEventImpl;

public final class CompactNsContext
extends BaseNsContext {
    final Location mLocation;
    final String[] mNamespaces;
    final int mNsLength;
    final int mFirstLocalNs;
    transient ArrayList<Namespace> mNsList;

    public CompactNsContext(Location loc, String[] namespaces, int nsLen, int firstLocal) {
        this.mLocation = loc;
        this.mNamespaces = namespaces;
        this.mNsLength = nsLen;
        this.mFirstLocalNs = firstLocal;
    }

    @Override
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

    @Override
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

    @Override
    public Iterator<String> doGetPrefixes(String nsURI) {
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
            return DataUtil.singletonIterator(first);
        }
        return DataUtil.emptyIterator();
    }

    @Override
    public Iterator<Namespace> getNamespaces() {
        if (this.mNsList == null) {
            int firstLocal = this.mFirstLocalNs;
            int len = this.mNsLength - firstLocal;
            if (len == 0) {
                return DataUtil.emptyIterator();
            }
            if (len == 2) {
                return DataUtil.singletonIterator(NamespaceEventImpl.constructNamespace((Location)this.mLocation, (String)this.mNamespaces[firstLocal], (String)this.mNamespaces[firstLocal + 1]));
            }
            ArrayList<NamespaceEventImpl> l = new ArrayList<NamespaceEventImpl>(len >> 1);
            String[] ns = this.mNamespaces;
            len = this.mNsLength;
            while (firstLocal < len) {
                l.add(NamespaceEventImpl.constructNamespace((Location)this.mLocation, (String)ns[firstLocal], (String)ns[firstLocal + 1]));
                firstLocal += 2;
            }
            this.mNsList = l;
        }
        return this.mNsList.iterator();
    }

    @Override
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

    @Override
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

