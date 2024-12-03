/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLDivElement;

public class HTMLDivElementImpl
extends HTMLElementImpl
implements HTMLDivElement {
    private static final long serialVersionUID = 2327098984177358833L;

    @Override
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }

    @Override
    public void setAlign(String string) {
        this.setAttribute("align", string);
    }

    public HTMLDivElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

