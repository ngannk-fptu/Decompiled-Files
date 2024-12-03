/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml.dom;

import org.apache.wml.WMLAElement;
import org.apache.wml.dom.WMLDocumentImpl;
import org.apache.wml.dom.WMLElementImpl;

public class WMLAElementImpl
extends WMLElementImpl
implements WMLAElement {
    private static final long serialVersionUID = 2628169803370301255L;

    public WMLAElementImpl(WMLDocumentImpl wMLDocumentImpl, String string) {
        super(wMLDocumentImpl, string);
    }

    @Override
    public void setHref(String string) {
        this.setAttribute("href", string);
    }

    @Override
    public String getHref() {
        return this.getAttribute("href");
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
    public void setId(String string) {
        this.setAttribute("id", string);
    }

    @Override
    public String getId() {
        return this.getAttribute("id");
    }
}

