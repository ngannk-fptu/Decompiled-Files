/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.apache.html.dom.HTMLFormControl;
import org.w3c.dom.Document;
import org.w3c.dom.html.HTMLObjectElement;

public class HTMLObjectElementImpl
extends HTMLElementImpl
implements HTMLObjectElement,
HTMLFormControl {
    private static final long serialVersionUID = 2276953229932965067L;

    @Override
    public String getCode() {
        return this.getAttribute("code");
    }

    @Override
    public void setCode(String string) {
        this.setAttribute("code", string);
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
    public String getArchive() {
        return this.getAttribute("archive");
    }

    @Override
    public void setArchive(String string) {
        this.setAttribute("archive", string);
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
    public String getCodeBase() {
        return this.getAttribute("codebase");
    }

    @Override
    public void setCodeBase(String string) {
        this.setAttribute("codebase", string);
    }

    @Override
    public String getCodeType() {
        return this.getAttribute("codetype");
    }

    @Override
    public void setCodeType(String string) {
        this.setAttribute("codetype", string);
    }

    @Override
    public String getData() {
        return this.getAttribute("data");
    }

    @Override
    public void setData(String string) {
        this.setAttribute("data", string);
    }

    @Override
    public boolean getDeclare() {
        return this.getBinary("declare");
    }

    @Override
    public void setDeclare(boolean bl) {
        this.setAttribute("declare", bl);
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
    public String getName() {
        return this.getAttribute("name");
    }

    @Override
    public void setName(String string) {
        this.setAttribute("name", string);
    }

    @Override
    public String getStandby() {
        return this.getAttribute("standby");
    }

    @Override
    public void setStandby(String string) {
        this.setAttribute("standby", string);
    }

    @Override
    public int getTabIndex() {
        try {
            return Integer.parseInt(this.getAttribute("tabindex"));
        }
        catch (NumberFormatException numberFormatException) {
            return 0;
        }
    }

    @Override
    public void setTabIndex(int n) {
        this.setAttribute("tabindex", String.valueOf(n));
    }

    @Override
    public String getType() {
        return this.getAttribute("type");
    }

    @Override
    public void setType(String string) {
        this.setAttribute("type", string);
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

    @Override
    public Document getContentDocument() {
        return null;
    }

    public HTMLObjectElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

