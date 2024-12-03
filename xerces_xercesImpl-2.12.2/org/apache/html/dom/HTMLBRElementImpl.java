/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLBRElement;

public class HTMLBRElementImpl
extends HTMLElementImpl
implements HTMLBRElement {
    private static final long serialVersionUID = 311960206282154750L;

    @Override
    public String getClear() {
        return this.capitalize(this.getAttribute("clear"));
    }

    @Override
    public void setClear(String string) {
        this.setAttribute("clear", string);
    }

    public HTMLBRElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

