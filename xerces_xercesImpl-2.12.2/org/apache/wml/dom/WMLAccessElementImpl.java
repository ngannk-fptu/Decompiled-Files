/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml.dom;

import org.apache.wml.WMLAccessElement;
import org.apache.wml.dom.WMLDocumentImpl;
import org.apache.wml.dom.WMLElementImpl;

public class WMLAccessElementImpl
extends WMLElementImpl
implements WMLAccessElement {
    private static final long serialVersionUID = -235627181817012806L;

    public WMLAccessElementImpl(WMLDocumentImpl wMLDocumentImpl, String string) {
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
    public void setDomain(String string) {
        this.setAttribute("domain", string);
    }

    @Override
    public String getDomain() {
        return this.getAttribute("domain");
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
    public void setPath(String string) {
        this.setAttribute("path", string);
    }

    @Override
    public String getPath() {
        return this.getAttribute("path");
    }
}

