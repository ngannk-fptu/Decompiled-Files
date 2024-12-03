/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLUListElement;

public class HTMLUListElementImpl
extends HTMLElementImpl
implements HTMLUListElement {
    @Override
    public boolean getCompact() {
        return this.getBinary("compact");
    }

    @Override
    public void setCompact(boolean compact) {
        this.setAttribute("compact", compact);
    }

    @Override
    public String getType() {
        return this.getAttribute("type");
    }

    @Override
    public void setType(String type) {
        this.setAttribute("type", type);
    }

    public HTMLUListElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

