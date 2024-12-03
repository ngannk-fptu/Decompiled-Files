/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLLIElement;

public class HTMLLIElementImpl
extends HTMLElementImpl
implements HTMLLIElement {
    @Override
    public String getType() {
        return this.getAttribute("type");
    }

    @Override
    public void setType(String type) {
        this.setAttribute("type", type);
    }

    @Override
    public int getValue() {
        return this.getInteger(this.getAttribute("value"));
    }

    @Override
    public void setValue(int value) {
        this.setAttribute("value", String.valueOf(value));
    }

    public HTMLLIElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

