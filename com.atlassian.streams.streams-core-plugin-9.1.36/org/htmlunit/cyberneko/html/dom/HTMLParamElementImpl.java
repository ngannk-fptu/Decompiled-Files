/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLParamElement;

public class HTMLParamElementImpl
extends HTMLElementImpl
implements HTMLParamElement {
    @Override
    public String getName() {
        return this.getAttribute("name");
    }

    @Override
    public void setName(String name) {
        this.setAttribute("name", name);
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
    public String getValue() {
        return this.getAttribute("value");
    }

    @Override
    public void setValue(String value) {
        this.setAttribute("value", value);
    }

    @Override
    public String getValueType() {
        return this.capitalize(this.getAttribute("valuetype"));
    }

    @Override
    public void setValueType(String valuetype) {
        this.setAttribute("valuetype", valuetype);
    }

    public HTMLParamElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

