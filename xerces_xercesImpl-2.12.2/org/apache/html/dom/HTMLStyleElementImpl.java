/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLStyleElement;

public class HTMLStyleElementImpl
extends HTMLElementImpl
implements HTMLStyleElement {
    private static final long serialVersionUID = -9001815754196124532L;

    @Override
    public boolean getDisabled() {
        return this.getBinary("disabled");
    }

    @Override
    public void setDisabled(boolean bl) {
        this.setAttribute("disabled", bl);
    }

    @Override
    public String getMedia() {
        return this.getAttribute("media");
    }

    @Override
    public void setMedia(String string) {
        this.setAttribute("media", string);
    }

    @Override
    public String getType() {
        return this.getAttribute("type");
    }

    @Override
    public void setType(String string) {
        this.setAttribute("type", string);
    }

    public HTMLStyleElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

