/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLTableColElement;

public class HTMLTableColElementImpl
extends HTMLElementImpl
implements HTMLTableColElement {
    @Override
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }

    @Override
    public void setAlign(String align) {
        this.setAttribute("align", align);
    }

    @Override
    public String getCh() {
        String ch = this.getAttribute("char");
        if (ch != null && ch.length() > 1) {
            ch = ch.substring(0, 1);
        }
        return ch;
    }

    @Override
    public void setCh(String ch) {
        if (ch != null && ch.length() > 1) {
            ch = ch.substring(0, 1);
        }
        this.setAttribute("char", ch);
    }

    @Override
    public String getChOff() {
        return this.getAttribute("charoff");
    }

    @Override
    public void setChOff(String chOff) {
        this.setAttribute("charoff", chOff);
    }

    @Override
    public int getSpan() {
        return this.getInteger(this.getAttribute("span"));
    }

    @Override
    public void setSpan(int span) {
        this.setAttribute("span", String.valueOf(span));
    }

    @Override
    public String getVAlign() {
        return this.capitalize(this.getAttribute("valign"));
    }

    @Override
    public void setVAlign(String vAlign) {
        this.setAttribute("valign", vAlign);
    }

    @Override
    public String getWidth() {
        return this.getAttribute("width");
    }

    @Override
    public void setWidth(String width) {
        this.setAttribute("width", width);
    }

    public HTMLTableColElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

