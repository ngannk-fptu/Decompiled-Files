/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLFormControl;
import org.w3c.dom.html.HTMLTextAreaElement;

public class HTMLTextAreaElementImpl
extends HTMLElementImpl
implements HTMLTextAreaElement,
HTMLFormControl {
    @Override
    public String getDefaultValue() {
        return this.getAttribute("default-value");
    }

    @Override
    public void setDefaultValue(String defaultValue) {
        this.setAttribute("default-value", defaultValue);
    }

    @Override
    public String getAccessKey() {
        String accessKey = this.getAttribute("accesskey");
        if (accessKey.length() > 1) {
            accessKey = accessKey.substring(0, 1);
        }
        return accessKey;
    }

    @Override
    public void setAccessKey(String accessKey) {
        if (accessKey != null && accessKey.length() > 1) {
            accessKey = accessKey.substring(0, 1);
        }
        this.setAttribute("accesskey", accessKey);
    }

    @Override
    public int getCols() {
        return this.getInteger(this.getAttribute("cols"));
    }

    @Override
    public void setCols(int cols) {
        this.setAttribute("cols", String.valueOf(cols));
    }

    @Override
    public boolean getDisabled() {
        return this.getBinary("disabled");
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.setAttribute("disabled", disabled);
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
    public boolean getReadOnly() {
        return this.getBinary("readonly");
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.setAttribute("readonly", readOnly);
    }

    @Override
    public int getRows() {
        return this.getInteger(this.getAttribute("rows"));
    }

    @Override
    public void setRows(int rows) {
        this.setAttribute("rows", String.valueOf(rows));
    }

    @Override
    public int getTabIndex() {
        return this.getInteger(this.getAttribute("tabindex"));
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
    public String getValue() {
        return this.getAttribute("value");
    }

    @Override
    public void setValue(String value) {
        this.setAttribute("value", value);
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

    public HTMLTextAreaElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

