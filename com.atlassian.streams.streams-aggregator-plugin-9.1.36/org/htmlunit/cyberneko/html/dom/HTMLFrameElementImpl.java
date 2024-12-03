/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.Document;
import org.w3c.dom.html.HTMLFrameElement;

public class HTMLFrameElementImpl
extends HTMLElementImpl
implements HTMLFrameElement {
    @Override
    public String getFrameBorder() {
        return this.getAttribute("frameborder");
    }

    @Override
    public void setFrameBorder(String frameBorder) {
        this.setAttribute("frameborder", frameBorder);
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
    public boolean getNoResize() {
        return this.getBinary("noresize");
    }

    @Override
    public void setNoResize(boolean noResize) {
        this.setAttribute("noresize", noResize);
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

    public HTMLFrameElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }

    @Override
    public Document getContentDocument() {
        return null;
    }
}

