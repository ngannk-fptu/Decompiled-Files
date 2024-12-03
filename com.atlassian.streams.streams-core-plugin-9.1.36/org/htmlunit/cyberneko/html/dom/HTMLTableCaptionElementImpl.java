/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLTableCaptionElement;

public class HTMLTableCaptionElementImpl
extends HTMLElementImpl
implements HTMLTableCaptionElement {
    @Override
    public String getAlign() {
        return this.getAttribute("align");
    }

    @Override
    public void setAlign(String align) {
        this.setAttribute("align", align);
    }

    public HTMLTableCaptionElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

