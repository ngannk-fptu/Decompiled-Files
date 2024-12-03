/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml.dom;

import org.apache.wml.WMLPostfieldElement;
import org.apache.wml.dom.WMLDocumentImpl;
import org.apache.wml.dom.WMLElementImpl;

public class WMLPostfieldElementImpl
extends WMLElementImpl
implements WMLPostfieldElement {
    private static final long serialVersionUID = -1169432003991642980L;

    public WMLPostfieldElementImpl(WMLDocumentImpl wMLDocumentImpl, String string) {
        super(wMLDocumentImpl, string);
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
    public void setClassName(String string) {
        this.setAttribute("class", string);
    }

    @Override
    public String getClassName() {
        return this.getAttribute("class");
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
    public void setName(String string) {
        this.setAttribute("name", string);
    }

    @Override
    public String getName() {
        return this.getAttribute("name");
    }
}

