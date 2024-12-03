/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.Document;
import org.w3c.dom.html.HTMLFrameElement;

public class HTMLFrameElementImpl
extends HTMLElementImpl
implements HTMLFrameElement {
    private static final long serialVersionUID = 635237057173695984L;

    @Override
    public String getFrameBorder() {
        return this.getAttribute("frameborder");
    }

    @Override
    public void setFrameBorder(String string) {
        this.setAttribute("frameborder", string);
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
    public boolean getNoResize() {
        return this.getBinary("noresize");
    }

    @Override
    public void setNoResize(boolean bl) {
        this.setAttribute("noresize", bl);
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
    public Document getContentDocument() {
        return null;
    }

    public HTMLFrameElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

