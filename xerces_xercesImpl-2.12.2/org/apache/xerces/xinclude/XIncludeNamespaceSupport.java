/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xinclude;

import org.apache.xerces.xinclude.MultipleScopeNamespaceSupport;
import org.apache.xerces.xni.NamespaceContext;

public class XIncludeNamespaceSupport
extends MultipleScopeNamespaceSupport {
    private boolean[] fValidContext = new boolean[8];

    public XIncludeNamespaceSupport() {
    }

    public XIncludeNamespaceSupport(NamespaceContext namespaceContext) {
        super(namespaceContext);
    }

    @Override
    public void pushContext() {
        super.pushContext();
        if (this.fCurrentContext + 1 == this.fValidContext.length) {
            boolean[] blArray = new boolean[this.fValidContext.length * 2];
            System.arraycopy(this.fValidContext, 0, blArray, 0, this.fValidContext.length);
            this.fValidContext = blArray;
        }
        this.fValidContext[this.fCurrentContext] = true;
    }

    public void setContextInvalid() {
        this.fValidContext[this.fCurrentContext] = false;
    }

    public String getURIFromIncludeParent(String string) {
        int n;
        for (n = this.fCurrentContext - 1; n > 0 && !this.fValidContext[n]; --n) {
        }
        return this.getURI(string, n);
    }
}

