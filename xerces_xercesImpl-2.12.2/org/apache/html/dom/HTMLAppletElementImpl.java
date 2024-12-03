/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLAppletElement;

public class HTMLAppletElementImpl
extends HTMLElementImpl
implements HTMLAppletElement {
    private static final long serialVersionUID = 8375794094117740967L;

    @Override
    public String getAlign() {
        return this.getAttribute("align");
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
    public String getArchive() {
        return this.getAttribute("archive");
    }

    @Override
    public void setArchive(String string) {
        this.setAttribute("archive", string);
    }

    @Override
    public String getCode() {
        return this.getAttribute("code");
    }

    @Override
    public void setCode(String string) {
        this.setAttribute("code", string);
    }

    @Override
    public String getCodeBase() {
        return this.getAttribute("codebase");
    }

    @Override
    public void setCodeBase(String string) {
        this.setAttribute("codebase", string);
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
        return this.getAttribute("height");
    }

    @Override
    public void setHspace(String string) {
        this.setAttribute("height", string);
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
    public String getObject() {
        return this.getAttribute("object");
    }

    @Override
    public void setObject(String string) {
        this.setAttribute("object", string);
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

    public HTMLAppletElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

