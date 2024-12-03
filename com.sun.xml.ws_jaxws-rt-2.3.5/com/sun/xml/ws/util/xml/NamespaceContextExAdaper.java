/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jvnet.staxex.NamespaceContextEx
 *  org.jvnet.staxex.NamespaceContextEx$Binding
 */
package com.sun.xml.ws.util.xml;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import org.jvnet.staxex.NamespaceContextEx;

public class NamespaceContextExAdaper
implements NamespaceContextEx {
    private final NamespaceContext nsContext;

    public NamespaceContextExAdaper(NamespaceContext nsContext) {
        this.nsContext = nsContext;
    }

    public Iterator<NamespaceContextEx.Binding> iterator() {
        throw new UnsupportedOperationException();
    }

    public String getNamespaceURI(String prefix) {
        return this.nsContext.getNamespaceURI(prefix);
    }

    public String getPrefix(String namespaceURI) {
        return this.nsContext.getPrefix(namespaceURI);
    }

    public Iterator getPrefixes(String namespaceURI) {
        return this.nsContext.getPrefixes(namespaceURI);
    }
}

