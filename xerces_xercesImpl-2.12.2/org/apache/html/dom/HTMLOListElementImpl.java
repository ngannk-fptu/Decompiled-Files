/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLOListElement;

public class HTMLOListElementImpl
extends HTMLElementImpl
implements HTMLOListElement {
    private static final long serialVersionUID = 1293750546025862146L;

    @Override
    public boolean getCompact() {
        return this.getBinary("compact");
    }

    @Override
    public void setCompact(boolean bl) {
        this.setAttribute("compact", bl);
    }

    @Override
    public int getStart() {
        return this.getInteger(this.getAttribute("start"));
    }

    @Override
    public void setStart(int n) {
        this.setAttribute("start", String.valueOf(n));
    }

    @Override
    public String getType() {
        return this.getAttribute("type");
    }

    @Override
    public void setType(String string) {
        this.setAttribute("type", string);
    }

    public HTMLOListElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

