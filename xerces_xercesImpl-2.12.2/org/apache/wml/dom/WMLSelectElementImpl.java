/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml.dom;

import org.apache.wml.WMLSelectElement;
import org.apache.wml.dom.WMLDocumentImpl;
import org.apache.wml.dom.WMLElementImpl;

public class WMLSelectElementImpl
extends WMLElementImpl
implements WMLSelectElement {
    private static final long serialVersionUID = 6489112443257247261L;

    public WMLSelectElementImpl(WMLDocumentImpl wMLDocumentImpl, String string) {
        super(wMLDocumentImpl, string);
    }

    @Override
    public void setMultiple(boolean bl) {
        this.setAttribute("multiple", bl);
    }

    @Override
    public boolean getMultiple() {
        return this.getAttribute("multiple", false);
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
    public void setTitle(String string) {
        this.setAttribute("title", string);
    }

    @Override
    public String getTitle() {
        return this.getAttribute("title");
    }

    @Override
    public void setIValue(String string) {
        this.setAttribute("ivalue", string);
    }

    @Override
    public String getIValue() {
        return this.getAttribute("ivalue");
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
    public void setIName(String string) {
        this.setAttribute("iname", string);
    }

    @Override
    public String getIName() {
        return this.getAttribute("iname");
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

