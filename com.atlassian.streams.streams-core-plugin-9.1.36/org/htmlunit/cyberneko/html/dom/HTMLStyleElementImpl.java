/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLStyleElement;

public class HTMLStyleElementImpl
extends HTMLElementImpl
implements HTMLStyleElement {
    @Override
    public boolean getDisabled() {
        return this.getBinary("disabled");
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.setAttribute("disabled", disabled);
    }

    @Override
    public String getMedia() {
        return this.getAttribute("media");
    }

    @Override
    public void setMedia(String media) {
        this.setAttribute("media", media);
    }

    @Override
    public String getType() {
        return this.getAttribute("type");
    }

    @Override
    public void setType(String type) {
        this.setAttribute("type", type);
    }

    public HTMLStyleElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

