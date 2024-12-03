/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLHtmlElement;

public class HTMLHtmlElementImpl
extends HTMLElementImpl
implements HTMLHtmlElement {
    private static final long serialVersionUID = -4489734201536616166L;

    @Override
    public String getVersion() {
        return this.capitalize(this.getAttribute("version"));
    }

    @Override
    public void setVersion(String string) {
        this.setAttribute("version", string);
    }

    public HTMLHtmlElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

