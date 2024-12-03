/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLQuoteElement;

public class HTMLQuoteElementImpl
extends HTMLElementImpl
implements HTMLQuoteElement {
    private static final long serialVersionUID = -67544811597906132L;

    @Override
    public String getCite() {
        return this.getAttribute("cite");
    }

    @Override
    public void setCite(String string) {
        this.setAttribute("cite", string);
    }

    public HTMLQuoteElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

