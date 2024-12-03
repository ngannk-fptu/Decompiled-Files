/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLFormControl;
import org.w3c.dom.Document;
import org.w3c.dom.html.HTMLObjectElement;

public class HTMLObjectElementImpl
extends HTMLElementImpl
implements HTMLObjectElement,
HTMLFormControl {
    @Override
    public String getCode() {
        return this.getAttribute("code");
    }

    @Override
    public void setCode(String code) {
        this.setAttribute("code", code);
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
    public String getArchive() {
        return this.getAttribute("archive");
    }

    @Override
    public void setArchive(String archive) {
        this.setAttribute("archive", archive);
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
    public String getCodeBase() {
        return this.getAttribute("codebase");
    }

    @Override
    public void setCodeBase(String codeBase) {
        this.setAttribute("codebase", codeBase);
    }

    @Override
    public String getCodeType() {
        return this.getAttribute("codetype");
    }

    @Override
    public void setCodeType(String codetype) {
        this.setAttribute("codetype", codetype);
    }

    @Override
    public String getData() {
        return this.getAttribute("data");
    }

    @Override
    public void setData(String data) {
        this.setAttribute("data", data);
    }

    @Override
    public boolean getDeclare() {
        return this.getBinary("declare");
    }

    @Override
    public void setDeclare(boolean declare) {
        this.setAttribute("declare", declare);
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
    public String getName() {
        return this.getAttribute("name");
    }

    @Override
    public void setName(String name) {
        this.setAttribute("name", name);
    }

    @Override
    public String getStandby() {
        return this.getAttribute("standby");
    }

    @Override
    public void setStandby(String standby) {
        this.setAttribute("standby", standby);
    }

    @Override
    public int getTabIndex() {
        try {
            return Integer.parseInt(this.getAttribute("tabindex"));
        }
        catch (NumberFormatException except) {
            return 0;
        }
    }

    @Override
    public void setTabIndex(int tabIndex) {
        this.setAttribute("tabindex", String.valueOf(tabIndex));
    }

    @Override
    public String getType() {
        return this.getAttribute("type");
    }

    @Override
    public void setType(String type) {
        this.setAttribute("type", type);
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

    public HTMLObjectElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }

    @Override
    public Document getContentDocument() {
        return null;
    }
}

