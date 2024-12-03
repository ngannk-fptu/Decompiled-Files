/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLDListElement;

public class HTMLDListElementImpl
extends HTMLElementImpl
implements HTMLDListElement {
    private static final long serialVersionUID = -2130005642453038604L;

    @Override
    public boolean getCompact() {
        return this.getBinary("compact");
    }

    @Override
    public void setCompact(boolean bl) {
        this.setAttribute("compact", bl);
    }

    public HTMLDListElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

