/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLIsIndexElement;

public class HTMLIsIndexElementImpl
extends HTMLElementImpl
implements HTMLIsIndexElement {
    @Override
    public String getPrompt() {
        return this.getAttribute("prompt");
    }

    @Override
    public void setPrompt(String prompt) {
        this.setAttribute("prompt", prompt);
    }

    public HTMLIsIndexElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

