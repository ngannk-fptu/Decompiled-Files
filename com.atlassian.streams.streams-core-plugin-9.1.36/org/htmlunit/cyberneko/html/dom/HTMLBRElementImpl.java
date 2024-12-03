/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLBRElement;

public class HTMLBRElementImpl
extends HTMLElementImpl
implements HTMLBRElement {
    @Override
    public String getClear() {
        return this.capitalize(this.getAttribute("clear"));
    }

    @Override
    public void setClear(String clear) {
        this.setAttribute("clear", clear);
    }

    public HTMLBRElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

