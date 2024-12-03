/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLBaseFontElement;

public class HTMLBaseFontElementImpl
extends HTMLElementImpl
implements HTMLBaseFontElement {
    @Override
    public String getColor() {
        return this.capitalize(this.getAttribute("color"));
    }

    @Override
    public void setColor(String color) {
        this.setAttribute("color", color);
    }

    @Override
    public String getFace() {
        return this.capitalize(this.getAttribute("face"));
    }

    @Override
    public void setFace(String face) {
        this.setAttribute("face", face);
    }

    @Override
    public String getSize() {
        return this.getAttribute("size");
    }

    @Override
    public void setSize(String size) {
        this.setAttribute("size", size);
    }

    public HTMLBaseFontElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

