/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLPreElement;

public class HTMLPreElementImpl
extends HTMLElementImpl
implements HTMLPreElement {
    @Override
    public int getWidth() {
        return this.getInteger(this.getAttribute("width"));
    }

    @Override
    public void setWidth(int width) {
        this.setAttribute("width", String.valueOf(width));
    }

    public HTMLPreElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

