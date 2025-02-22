/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.bind.v2.runtime;

import com.sun.xml.bind.v2.runtime.XMLSerializer;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;

final class StAXPostInitAction
implements Runnable {
    private final XMLStreamWriter xsw;
    private final XMLEventWriter xew;
    private final NamespaceContext nsc;
    private final XMLSerializer serializer;

    StAXPostInitAction(XMLStreamWriter xsw, XMLSerializer serializer) {
        this.xsw = xsw;
        this.xew = null;
        this.nsc = null;
        this.serializer = serializer;
    }

    StAXPostInitAction(XMLEventWriter xew, XMLSerializer serializer) {
        this.xsw = null;
        this.xew = xew;
        this.nsc = null;
        this.serializer = serializer;
    }

    StAXPostInitAction(NamespaceContext nsc, XMLSerializer serializer) {
        this.xsw = null;
        this.xew = null;
        this.nsc = nsc;
        this.serializer = serializer;
    }

    @Override
    public void run() {
        NamespaceContext ns = this.nsc;
        if (this.xsw != null) {
            ns = this.xsw.getNamespaceContext();
        }
        if (this.xew != null) {
            ns = this.xew.getNamespaceContext();
        }
        if (ns == null) {
            return;
        }
        for (String nsUri : this.serializer.grammar.nameList.namespaceURIs) {
            String p = ns.getPrefix(nsUri);
            if (p == null) continue;
            this.serializer.addInscopeBinding(nsUri, p);
        }
    }
}

