/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLHRElement;

public class HTMLHRElementImpl
extends HTMLElementImpl
implements HTMLHRElement {
    @Override
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }

    @Override
    public void setAlign(String align) {
        this.setAttribute("align", align);
    }

    @Override
    public boolean getNoShade() {
        return this.getBinary("noshade");
    }

    @Override
    public void setNoShade(boolean noShade) {
        this.setAttribute("noshade", noShade);
    }

    @Override
    public String getSize() {
        return this.getAttribute("size");
    }

    @Override
    public void setSize(String size) {
        this.setAttribute("size", size);
    }

    @Override
    public String getWidth() {
        return this.getAttribute("width");
    }

    @Override
    public void setWidth(String width) {
        this.setAttribute("width", width);
    }

    public HTMLHRElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

