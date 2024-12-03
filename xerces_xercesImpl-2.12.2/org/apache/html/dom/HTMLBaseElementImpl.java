/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLBaseElement;

public class HTMLBaseElementImpl
extends HTMLElementImpl
implements HTMLBaseElement {
    private static final long serialVersionUID = -396648580810072153L;

    @Override
    public String getHref() {
        return this.getAttribute("href");
    }

    @Override
    public void setHref(String string) {
        this.setAttribute("href", string);
    }

    @Override
    public String getTarget() {
        return this.getAttribute("target");
    }

    @Override
    public void setTarget(String string) {
        this.setAttribute("target", string);
    }

    public HTMLBaseElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

