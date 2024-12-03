/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLImageElement;

public class HTMLImageElementImpl
extends HTMLElementImpl
implements HTMLImageElement {
    @Override
    public String getLowSrc() {
        return this.getAttribute("lowsrc");
    }

    @Override
    public void setLowSrc(String lowSrc) {
        this.setAttribute("lowsrc", lowSrc);
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
    public String getName() {
        return this.getAttribute("name");
    }

    @Override
    public void setName(String name) {
        this.setAttribute("name", name);
    }

    @Override
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }

    @Override
    public void setAlign(String align) {
        this.setAttribute("align", align);
    }

    @Override
    public String getAlt() {
        return this.getAttribute("alt");
    }

    @Override
    public void setAlt(String alt) {
        this.setAttribute("alt", alt);
    }

    @Override
    public String getBorder() {
        return this.getAttribute("border");
    }

    @Override
    public void setBorder(String border) {
        this.setAttribute("border", border);
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
    public String getHspace() {
        return this.getAttribute("hspace");
    }

    @Override
    public void setHspace(String hspace) {
        this.setAttribute("hspace", hspace);
    }

    @Override
    public boolean getIsMap() {
        return this.getBinary("ismap");
    }

    @Override
    public void setIsMap(boolean isMap) {
        this.setAttribute("ismap", isMap);
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
    public String getUseMap() {
        return this.getAttribute("useMap");
    }

    @Override
    public void setUseMap(String useMap) {
        this.setAttribute("useMap", useMap);
    }

    @Override
    public String getVspace() {
        return this.getAttribute("vspace");
    }

    @Override
    public void setVspace(String vspace) {
        this.setAttribute("vspace", vspace);
    }

    @Override
    public String getWidth() {
        return this.getAttribute("width");
    }

    @Override
    public void setWidth(String width) {
        this.setAttribute("width", width);
    }

    public HTMLImageElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

