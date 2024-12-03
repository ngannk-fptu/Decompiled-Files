/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml.dom;

import org.apache.wml.WMLImgElement;
import org.apache.wml.dom.WMLDocumentImpl;
import org.apache.wml.dom.WMLElementImpl;

public class WMLImgElementImpl
extends WMLElementImpl
implements WMLImgElement {
    private static final long serialVersionUID = -500092034867051550L;

    public WMLImgElementImpl(WMLDocumentImpl wMLDocumentImpl, String string) {
        super(wMLDocumentImpl, string);
    }

    @Override
    public void setWidth(String string) {
        this.setAttribute("width", string);
    }

    @Override
    public String getWidth() {
        return this.getAttribute("width");
    }

    @Override
    public void setClassName(String string) {
        this.setAttribute("class", string);
    }

    @Override
    public String getClassName() {
        return this.getAttribute("class");
    }

    @Override
    public void setXmlLang(String string) {
        this.setAttribute("xml:lang", string);
    }

    @Override
    public String getXmlLang() {
        return this.getAttribute("xml:lang");
    }

    @Override
    public void setLocalSrc(String string) {
        this.setAttribute("localsrc", string);
    }

    @Override
    public String getLocalSrc() {
        return this.getAttribute("localsrc");
    }

    @Override
    public void setHeight(String string) {
        this.setAttribute("height", string);
    }

    @Override
    public String getHeight() {
        return this.getAttribute("height");
    }

    @Override
    public void setAlign(String string) {
        this.setAttribute("align", string);
    }

    @Override
    public String getAlign() {
        return this.getAttribute("align");
    }

    @Override
    public void setVspace(String string) {
        this.setAttribute("vspace", string);
    }

    @Override
    public String getVspace() {
        return this.getAttribute("vspace");
    }

    @Override
    public void setAlt(String string) {
        this.setAttribute("alt", string);
    }

    @Override
    public String getAlt() {
        return this.getAttribute("alt");
    }

    @Override
    public void setId(String string) {
        this.setAttribute("id", string);
    }

    @Override
    public String getId() {
        return this.getAttribute("id");
    }

    @Override
    public void setHspace(String string) {
        this.setAttribute("hspace", string);
    }

    @Override
    public String getHspace() {
        return this.getAttribute("hspace");
    }

    @Override
    public void setSrc(String string) {
        this.setAttribute("src", string);
    }

    @Override
    public String getSrc() {
        return this.getAttribute("src");
    }
}

