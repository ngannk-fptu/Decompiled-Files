/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLHRElement;

public class HTMLHRElementImpl
extends HTMLElementImpl
implements HTMLHRElement {
    private static final long serialVersionUID = -4210053417678939270L;

    @Override
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }

    @Override
    public void setAlign(String string) {
        this.setAttribute("align", string);
    }

    @Override
    public boolean getNoShade() {
        return this.getBinary("noshade");
    }

    @Override
    public void setNoShade(boolean bl) {
        this.setAttribute("noshade", bl);
    }

    @Override
    public String getSize() {
        return this.getAttribute("size");
    }

    @Override
    public void setSize(String string) {
        this.setAttribute("size", string);
    }

    @Override
    public String getWidth() {
        return this.getAttribute("width");
    }

    @Override
    public void setWidth(String string) {
        this.setAttribute("width", string);
    }

    public HTMLHRElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

