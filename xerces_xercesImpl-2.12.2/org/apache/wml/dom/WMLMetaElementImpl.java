/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml.dom;

import org.apache.wml.WMLMetaElement;
import org.apache.wml.dom.WMLDocumentImpl;
import org.apache.wml.dom.WMLElementImpl;

public class WMLMetaElementImpl
extends WMLElementImpl
implements WMLMetaElement {
    private static final long serialVersionUID = -2791663042188681846L;

    public WMLMetaElementImpl(WMLDocumentImpl wMLDocumentImpl, String string) {
        super(wMLDocumentImpl, string);
    }

    @Override
    public void setForua(boolean bl) {
        this.setAttribute("forua", bl);
    }

    @Override
    public boolean getForua() {
        return this.getAttribute("forua", false);
    }

    @Override
    public void setScheme(String string) {
        this.setAttribute("scheme", string);
    }

    @Override
    public String getScheme() {
        return this.getAttribute("scheme");
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
    public void setHttpEquiv(String string) {
        this.setAttribute("http-equiv", string);
    }

    @Override
    public String getHttpEquiv() {
        return this.getAttribute("http-equiv");
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
    public void setContent(String string) {
        this.setAttribute("content", string);
    }

    @Override
    public String getContent() {
        return this.getAttribute("content");
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

