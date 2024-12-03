/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLFormControl;
import org.w3c.dom.html.HTMLInputElement;

public class HTMLInputElementImpl
extends HTMLElementImpl
implements HTMLInputElement,
HTMLFormControl {
    @Override
    public String getDefaultValue() {
        return this.getAttribute("defaultValue");
    }

    @Override
    public void setDefaultValue(String defaultValue) {
        this.setAttribute("defaultValue", defaultValue);
    }

    @Override
    public boolean getDefaultChecked() {
        return this.getBinary("defaultChecked");
    }

    @Override
    public void setDefaultChecked(boolean defaultChecked) {
        this.setAttribute("defaultChecked", defaultChecked);
    }

    @Override
    public String getAccept() {
        return this.getAttribute("accept");
    }

    @Override
    public void setAccept(String accept) {
        this.setAttribute("accept", accept);
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
    public boolean getChecked() {
        return this.getBinary("checked");
    }

    @Override
    public void setChecked(boolean checked) {
        this.setAttribute("checked", checked);
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
    public int getMaxLength() {
        return this.getInteger(this.getAttribute("maxlength"));
    }

    @Override
    public void setMaxLength(int maxLength) {
        this.setAttribute("maxlength", String.valueOf(maxLength));
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
    public String getSize() {
        return this.getAttribute("size");
    }

    @Override
    public void setSize(String size) {
        this.setAttribute("size", size);
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
    public String getUseMap() {
        return this.getAttribute("useMap");
    }

    @Override
    public void setUseMap(String useMap) {
        this.setAttribute("useMap", useMap);
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

    @Override
    public void click() {
    }

    public HTMLInputElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

