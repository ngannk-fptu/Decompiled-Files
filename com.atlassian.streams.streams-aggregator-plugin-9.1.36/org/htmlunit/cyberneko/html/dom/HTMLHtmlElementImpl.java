/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLHtmlElement;

public class HTMLHtmlElementImpl
extends HTMLElementImpl
implements HTMLHtmlElement {
    @Override
    public String getVersion() {
        return this.capitalize(this.getAttribute("version"));
    }

    @Override
    public void setVersion(String version) {
        this.setAttribute("version", version);
    }

    public HTMLHtmlElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

