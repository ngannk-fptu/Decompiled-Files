/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLAppletElement;

public class HTMLAppletElementImpl
extends HTMLElementImpl
implements HTMLAppletElement {
    @Override
    public String getAlign() {
        return this.getAttribute("align");
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
    public String getArchive() {
        return this.getAttribute("archive");
    }

    @Override
    public void setArchive(String archive) {
        this.setAttribute("archive", archive);
    }

    @Override
    public String getCode() {
        return this.getAttribute("code");
    }

    @Override
    public void setCode(String code) {
        this.setAttribute("code", code);
    }

    @Override
    public String getCodeBase() {
        return this.getAttribute("codebase");
    }

    @Override
    public void setCodeBase(String codeBase) {
        this.setAttribute("codebase", codeBase);
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
        return this.getAttribute("height");
    }

    @Override
    public void setHspace(String height) {
        this.setAttribute("height", height);
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
    public String getObject() {
        return this.getAttribute("object");
    }

    @Override
    public void setObject(String object) {
        this.setAttribute("object", object);
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

    public HTMLAppletElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

