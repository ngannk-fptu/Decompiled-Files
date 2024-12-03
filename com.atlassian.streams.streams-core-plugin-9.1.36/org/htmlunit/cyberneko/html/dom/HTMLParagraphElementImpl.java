/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLParagraphElement;

public class HTMLParagraphElementImpl
extends HTMLElementImpl
implements HTMLParagraphElement {
    @Override
    public String getAlign() {
        return this.getAttribute("align");
    }

    @Override
    public void setAlign(String align) {
        this.setAttribute("align", align);
    }

    public HTMLParagraphElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

