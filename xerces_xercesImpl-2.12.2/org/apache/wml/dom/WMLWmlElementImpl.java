/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml.dom;

import org.apache.wml.WMLWmlElement;
import org.apache.wml.dom.WMLDocumentImpl;
import org.apache.wml.dom.WMLElementImpl;

public class WMLWmlElementImpl
extends WMLElementImpl
implements WMLWmlElement {
    private static final long serialVersionUID = -7008023851895920651L;

    public WMLWmlElementImpl(WMLDocumentImpl wMLDocumentImpl, String string) {
        super(wMLDocumentImpl, string);
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
    public void setId(String string) {
        this.setAttribute("id", string);
    }

    @Override
    public String getId() {
        return this.getAttribute("id");
    }
}

