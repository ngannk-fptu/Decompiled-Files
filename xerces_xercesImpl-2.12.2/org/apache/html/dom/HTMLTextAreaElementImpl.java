/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.apache.html.dom.HTMLFormControl;
import org.w3c.dom.html.HTMLTextAreaElement;

public class HTMLTextAreaElementImpl
extends HTMLElementImpl
implements HTMLTextAreaElement,
HTMLFormControl {
    private static final long serialVersionUID = -6737778308542678104L;

    @Override
    public String getDefaultValue() {
        return this.getAttribute("default-value");
    }

    @Override
    public void setDefaultValue(String string) {
        this.setAttribute("default-value", string);
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
    public int getCols() {
        return this.getInteger(this.getAttribute("cols"));
    }

    @Override
    public void setCols(int n) {
        this.setAttribute("cols", String.valueOf(n));
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
    public int getRows() {
        return this.getInteger(this.getAttribute("rows"));
    }

    @Override
    public void setRows(int n) {
        this.setAttribute("rows", String.valueOf(n));
    }

    @Override
    public int getTabIndex() {
        return this.getInteger(this.getAttribute("tabindex"));
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

    public HTMLTextAreaElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

