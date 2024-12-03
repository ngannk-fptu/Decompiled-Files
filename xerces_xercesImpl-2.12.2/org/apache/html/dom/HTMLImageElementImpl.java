/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLImageElement;

public class HTMLImageElementImpl
extends HTMLElementImpl
implements HTMLImageElement {
    private static final long serialVersionUID = 1424360710977241315L;

    @Override
    public String getLowSrc() {
        return this.getAttribute("lowsrc");
    }

    @Override
    public void setLowSrc(String string) {
        this.setAttribute("lowsrc", string);
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
    public String getName() {
        return this.getAttribute("name");
    }

    @Override
    public void setName(String string) {
        this.setAttribute("name", string);
    }

    @Override
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }

    @Override
    public void setAlign(String string) {
        this.setAttribute("align", string);
    }

    @Override
    public String getAlt() {
        return this.getAttribute("alt");
    }

    @Override
    public void setAlt(String string) {
        this.setAttribute("alt", string);
    }

    @Override
    public String getBorder() {
        return this.getAttribute("border");
    }

    @Override
    public void setBorder(String string) {
        this.setAttribute("border", string);
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
    public String getHspace() {
        return this.getAttribute("hspace");
    }

    @Override
    public void setHspace(String string) {
        this.setAttribute("hspace", string);
    }

    @Override
    public boolean getIsMap() {
        return this.getBinary("ismap");
    }

    @Override
    public void setIsMap(boolean bl) {
        this.setAttribute("ismap", bl);
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
    public String getUseMap() {
        return this.getAttribute("useMap");
    }

    @Override
    public void setUseMap(String string) {
        this.setAttribute("useMap", string);
    }

    @Override
    public String getVspace() {
        return this.getAttribute("vspace");
    }

    @Override
    public void setVspace(String string) {
        this.setAttribute("vspace", string);
    }

    @Override
    public String getWidth() {
        return this.getAttribute("width");
    }

    @Override
    public void setWidth(String string) {
        this.setAttribute("width", string);
    }

    public HTMLImageElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

