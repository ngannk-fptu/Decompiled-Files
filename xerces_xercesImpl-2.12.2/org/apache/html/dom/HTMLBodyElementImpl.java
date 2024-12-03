/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLBodyElement;

public class HTMLBodyElementImpl
extends HTMLElementImpl
implements HTMLBodyElement {
    private static final long serialVersionUID = 9058852459426595202L;

    @Override
    public String getALink() {
        return this.getAttribute("alink");
    }

    @Override
    public void setALink(String string) {
        this.setAttribute("alink", string);
    }

    @Override
    public String getBackground() {
        return this.getAttribute("background");
    }

    @Override
    public void setBackground(String string) {
        this.setAttribute("background", string);
    }

    @Override
    public String getBgColor() {
        return this.getAttribute("bgcolor");
    }

    @Override
    public void setBgColor(String string) {
        this.setAttribute("bgcolor", string);
    }

    @Override
    public String getLink() {
        return this.getAttribute("link");
    }

    @Override
    public void setLink(String string) {
        this.setAttribute("link", string);
    }

    @Override
    public String getText() {
        return this.getAttribute("text");
    }

    @Override
    public void setText(String string) {
        this.setAttribute("text", string);
    }

    @Override
    public String getVLink() {
        return this.getAttribute("vlink");
    }

    @Override
    public void setVLink(String string) {
        this.setAttribute("vlink", string);
    }

    public HTMLBodyElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

