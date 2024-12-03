/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLBaseFontElement;

public class HTMLBaseFontElementImpl
extends HTMLElementImpl
implements HTMLBaseFontElement {
    private static final long serialVersionUID = -3650249921091097229L;

    @Override
    public String getColor() {
        return this.capitalize(this.getAttribute("color"));
    }

    @Override
    public void setColor(String string) {
        this.setAttribute("color", string);
    }

    @Override
    public String getFace() {
        return this.capitalize(this.getAttribute("face"));
    }

    @Override
    public void setFace(String string) {
        this.setAttribute("face", string);
    }

    @Override
    public String getSize() {
        return this.getAttribute("size");
    }

    @Override
    public void setSize(String string) {
        this.setAttribute("size", string);
    }

    public HTMLBaseFontElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

