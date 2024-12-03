/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.apache.html.dom.HTMLFormControl;
import org.w3c.dom.html.HTMLInputElement;

public class HTMLInputElementImpl
extends HTMLElementImpl
implements HTMLInputElement,
HTMLFormControl {
    private static final long serialVersionUID = 640139325394332007L;

    @Override
    public String getDefaultValue() {
        return this.getAttribute("defaultValue");
    }

    @Override
    public void setDefaultValue(String string) {
        this.setAttribute("defaultValue", string);
    }

    @Override
    public boolean getDefaultChecked() {
        return this.getBinary("defaultChecked");
    }

    @Override
    public void setDefaultChecked(boolean bl) {
        this.setAttribute("defaultChecked", bl);
    }

    @Override
    public String getAccept() {
        return this.getAttribute("accept");
    }

    @Override
    public void setAccept(String string) {
        this.setAttribute("accept", string);
    }

    @Override
    public String getAccessKey() {
        String string = this.getAttribute("accesskey");
        if (string != null && string.length() > 1) {
            string = string.substring(0, 1);
        }
        return string;
    }

    @Override
    public void setAccessKey(String string) {
        if (string != null && string.length() > 1) {
            string = string.substring(0, 1);
        }
        this.setAttribute("accesskey", string);
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
    public boolean getChecked() {
        return this.getBinary("checked");
    }

    @Override
    public void setChecked(boolean bl) {
        this.setAttribute("checked", bl);
    }

    @Override
    public boolean getDisabled() {
        return this.getBinary("disabled");
    }

    @Override
    public void setDisabled(boolean bl) {
        this.setAttribute("disabled", bl);
    }

    @Override
    public int getMaxLength() {
        return this.getInteger(this.getAttribute("maxlength"));
    }

    @Override
    public void setMaxLength(int n) {
        this.setAttribute("maxlength", String.valueOf(n));
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
    public boolean getReadOnly() {
        return this.getBinary("readonly");
    }

    @Override
    public void setReadOnly(boolean bl) {
        this.setAttribute("readonly", bl);
    }

    @Override
    public String getSize() {
        return this.getAttribute("size");
    }

    @Override
    public void setSize(String string) {
        this.setAttribute("size", string);
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
    public String getUseMap() {
        return this.getAttribute("useMap");
    }

    @Override
    public void setUseMap(String string) {
        this.setAttribute("useMap", string);
    }

    @Override
    public String getValue() {
        return this.getAttribute("value");
    }

    @Override
    public void setValue(String string) {
        this.setAttribute("value", string);
    }

    @Override
    public void blur() {
    }

    @Override
    public void focus() {
    }

    @Override
    public void select() {
    }

    @Override
    public void click() {
    }

    public HTMLInputElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

