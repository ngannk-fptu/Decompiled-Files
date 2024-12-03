/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLQuoteElement;

public class HTMLQuoteElementImpl
extends HTMLElementImpl
implements HTMLQuoteElement {
    @Override
    public String getCite() {
        return this.getAttribute("cite");
    }

    @Override
    public void setCite(String cite) {
        this.setAttribute("cite", cite);
    }

    public HTMLQuoteElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

