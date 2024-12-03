/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLParagraphElement;

public class HTMLParagraphElementImpl
extends HTMLElementImpl
implements HTMLParagraphElement {
    private static final long serialVersionUID = 8075287150683866287L;

    @Override
    public String getAlign() {
        return this.getAttribute("align");
    }

    @Override
    public void setAlign(String string) {
        this.setAttribute("align", string);
    }

    public HTMLParagraphElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

