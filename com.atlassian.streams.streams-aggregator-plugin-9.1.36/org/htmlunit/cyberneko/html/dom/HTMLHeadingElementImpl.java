/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLHeadingElement;

public class HTMLHeadingElementImpl
extends HTMLElementImpl
implements HTMLHeadingElement {
    @Override
    public String getAlign() {
        return this.getCapitalized("align");
    }

    @Override
    public void setAlign(String align) {
        this.setAttribute("align", align);
    }

    public HTMLHeadingElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

