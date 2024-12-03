/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLOListElement;

public class HTMLOListElementImpl
extends HTMLElementImpl
implements HTMLOListElement {
    @Override
    public boolean getCompact() {
        return this.getBinary("compact");
    }

    @Override
    public void setCompact(boolean compact) {
        this.setAttribute("compact", compact);
    }

    @Override
    public int getStart() {
        return this.getInteger(this.getAttribute("start"));
    }

    @Override
    public void setStart(int start) {
        this.setAttribute("start", String.valueOf(start));
    }

    @Override
    public String getType() {
        return this.getAttribute("type");
    }

    @Override
    public void setType(String type) {
        this.setAttribute("type", type);
    }

    public HTMLOListElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

