/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.Document;
import org.w3c.dom.html.HTMLIFrameElement;

public class HTMLIFrameElementImpl
extends HTMLElementImpl
implements HTMLIFrameElement {
    private static final long serialVersionUID = 2393622754706230429L;

    @Override
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }

    @Override
    public void setAlign(String string) {
        this.setAttribute("align", string);
    }

    @Override
    public String getFrameBorder() {
        return this.getAttribute("frameborder");
    }

    @Override
    public void setFrameBorder(String string) {
        this.setAttribute("frameborder", string);
    }

    @Override
    public String getHeight() {
        return this.getAttribute("height");
    }

    @Override
    public void setHeight(String string) {
        this.setAttribute("height", string);
    }

    @Override
    public String getLongDesc() {
        return this.getAttribute("longdesc");
    }

    @Override
    public void setLongDesc(String string) {
        this.setAttribute("longdesc", string);
    }

    @Override
    public String getMarginHeight() {
        return this.getAttribute("marginheight");
    }

    @Override
    public void setMarginHeight(String string) {
        this.setAttribute("marginheight", string);
    }

    @Override
    public String getMarginWidth() {
        return this.getAttribute("marginwidth");
    }

    @Override
    public void setMarginWidth(String string) {
        this.setAttribute("marginwidth", string);
    }

    @Override
    public String getName() {
        return this.getAttribute("name");
    }

    @Override
    public void setName(String string) {
        this.setAttribute("name", string);
    }

    @Override
    public String getScrolling() {
        return this.capitalize(this.getAttribute("scrolling"));
    }

    @Override
    public void setScrolling(String string) {
        this.setAttribute("scrolling", string);
    }

    @Override
    public String getSrc() {
        return this.getAttribute("src");
    }

    @Override
    public void setSrc(String string) {
        this.setAttribute("src", string);
    }

    @Override
    public String getWidth() {
        return this.getAttribute("width");
    }

    @Override
    public void setWidth(String string) {
        this.setAttribute("width", string);
    }

    @Override
    public Document getContentDocument() {
        return null;
    }

    public HTMLIFrameElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

