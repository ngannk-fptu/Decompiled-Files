/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLBodyElement;

public class HTMLBodyElementImpl
extends HTMLElementImpl
implements HTMLBodyElement {
    @Override
    public String getALink() {
        return this.getAttribute("alink");
    }

    @Override
    public void setALink(String aLink) {
        this.setAttribute("alink", aLink);
    }

    @Override
    public String getBackground() {
        return this.getAttribute("background");
    }

    @Override
    public void setBackground(String background) {
        this.setAttribute("background", background);
    }

    @Override
    public String getBgColor() {
        return this.getAttribute("bgcolor");
    }

    @Override
    public void setBgColor(String bgColor) {
        this.setAttribute("bgcolor", bgColor);
    }

    @Override
    public String getLink() {
        return this.getAttribute("link");
    }

    @Override
    public void setLink(String link) {
        this.setAttribute("link", link);
    }

    @Override
    public String getText() {
        return this.getAttribute("text");
    }

    @Override
    public void setText(String text) {
        this.setAttribute("text", text);
    }

    @Override
    public String getVLink() {
        return this.getAttribute("vlink");
    }

    @Override
    public void setVLink(String vLink) {
        this.setAttribute("vlink", vLink);
    }

    public HTMLBodyElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

