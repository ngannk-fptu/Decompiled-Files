/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLPreElement;

public class HTMLPreElementImpl
extends HTMLElementImpl
implements HTMLPreElement {
    private static final long serialVersionUID = -4195360849946217644L;

    @Override
    public int getWidth() {
        return this.getInteger(this.getAttribute("width"));
    }

    @Override
    public void setWidth(int n) {
        this.setAttribute("width", String.valueOf(n));
    }

    public HTMLPreElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

