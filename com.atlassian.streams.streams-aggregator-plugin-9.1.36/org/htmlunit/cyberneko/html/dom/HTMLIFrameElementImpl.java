/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.Document;
import org.w3c.dom.html.HTMLIFrameElement;

public class HTMLIFrameElementImpl
extends HTMLElementImpl
implements HTMLIFrameElement {
    @Override
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }

    @Override
    public void setAlign(String align) {
        this.setAttribute("align", align);
    }

    @Override
    public String getFrameBorder() {
        return this.getAttribute("frameborder");
    }

    @Override
    public void setFrameBorder(String frameBorder) {
        this.setAttribute("frameborder", frameBorder);
    }

    @Override
    public String getHeight() {
        return this.getAttribute("height");
    }

    @Override
    public void setHeight(String height) {
        this.setAttribute("height", height);
    }

    @Override
    public String getLongDesc() {
        return this.getAttribute("longdesc");
    }

    @Override
    public void setLongDesc(String longDesc) {
        this.setAttribute("longdesc", longDesc);
    }

    @Override
    public String getMarginHeight() {
        return this.getAttribute("marginheight");
    }

    @Override
    public void setMarginHeight(String marginHeight) {
        this.setAttribute("marginheight", marginHeight);
    }

    @Override
    public String getMarginWidth() {
        return this.getAttribute("marginwidth");
    }

    @Override
    public void setMarginWidth(String marginWidth) {
        this.setAttribute("marginwidth", marginWidth);
    }

    @Override
    public String getName() {
        return this.getAttribute("name");
    }

    @Override
    public void setName(String name) {
        this.setAttribute("name", name);
    }

    @Override
    public String getScrolling() {
        return this.capitalize(this.getAttribute("scrolling"));
    }

    @Override
    public void setScrolling(String scrolling) {
        this.setAttribute("scrolling", scrolling);
    }

    @Override
    public String getSrc() {
        return this.getAttribute("src");
    }

    @Override
    public void setSrc(String src) {
        this.setAttribute("src", src);
    }

    @Override
    public String getWidth() {
        return this.getAttribute("width");
    }

    @Override
    public void setWidth(String width) {
        this.setAttribute("width", width);
    }

    public HTMLIFrameElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }

    @Override
    public Document getContentDocument() {
        return null;
    }
}

