/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.util.Collections;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public class NamespaceContextAdapter
implements NamespaceContext {
    protected NamespaceContext namespaceCtx;

    public NamespaceContextAdapter() {
    }

    public NamespaceContextAdapter(NamespaceContext namespaceCtx) {
        this.namespaceCtx = namespaceCtx;
    }

    public String getNamespaceURI(String prefix) {
        if (this.namespaceCtx != null) {
            return this.namespaceCtx.getNamespaceURI(prefix);
        }
        return null;
    }

    public String getPrefix(String nsURI) {
        if (this.namespaceCtx != null) {
            return this.namespaceCtx.getPrefix(nsURI);
        }
        return null;
    }

    public Iterator getPrefixes(String nsURI) {
        if (this.namespaceCtx != null) {
            return this.namespaceCtx.getPrefixes(nsURI);
        }
        return Collections.EMPTY_LIST.iterator();
    }
}

