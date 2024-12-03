/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLIsIndexElement;

public class HTMLIsIndexElementImpl
extends HTMLElementImpl
implements HTMLIsIndexElement {
    private static final long serialVersionUID = 3073521742049689699L;

    @Override
    public String getPrompt() {
        return this.getAttribute("prompt");
    }

    @Override
    public void setPrompt(String string) {
        this.setAttribute("prompt", string);
    }

    public HTMLIsIndexElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

