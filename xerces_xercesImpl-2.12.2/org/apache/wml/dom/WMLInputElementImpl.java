/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml.dom;

import org.apache.wml.WMLInputElement;
import org.apache.wml.dom.WMLDocumentImpl;
import org.apache.wml.dom.WMLElementImpl;

public class WMLInputElementImpl
extends WMLElementImpl
implements WMLInputElement {
    private static final long serialVersionUID = 2897319793637966619L;

    public WMLInputElementImpl(WMLDocumentImpl wMLDocumentImpl, String string) {
        super(wMLDocumentImpl, string);
    }

    @Override
    public void setSize(int n) {
        this.setAttribute("size", n);
    }

    @Override
    public int getSize() {
        return this.getAttribute("size", 0);
    }

    @Override
    public void setFormat(String string) {
        this.setAttribute("format", string);
    }

    @Override
    public String getFormat() {
        return this.getAttribute("format");
    }

    @Override
    public void setValue(String string) {
        this.setAttribute("value", string);
    }

    @Override
    public String getValue() {
        return this.getAttribute("value");
    }

    @Override
    public void setMaxLength(int n) {
        this.setAttribute("maxlength", n);
    }

    @Override
    public int getMaxLength() {
        return this.getAttribute("maxlength", 0);
    }

    @Override
    public void setTabIndex(int n) {
        this.setAttribute("tabindex", n);
    }

    @Override
    public int getTabIndex() {
        return this.getAttribute("tabindex", 0);
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
    public void setEmptyOk(boolean bl) {
        this.setAttribute("emptyok", bl);
    }

    @Override
    public boolean getEmptyOk() {
        return this.getAttribute("emptyok", false);
    }

    @Override
    public void setTitle(String string) {
        this.setAttribute("title", string);
    }

    @Override
    public String getTitle() {
        return this.getAttribute("title");
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
    public void setType(String string) {
        this.setAttribute("type", string);
    }

    @Override
    public String getType() {
        return this.getAttribute("type");
    }

    @Override
    public void setName(String string) {
        this.setAttribute("name", string);
    }

    @Override
    public String getName() {
        return this.getAttribute("name");
    }
}

